package org.myas.victims.search.index;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import org.myas.victims.search.index.id.IdGenerator;

/**
 * Created by Mykhailo Yashchuk on 11.04.2017.
 */
public abstract class AbstractIndex<E> implements Index<E> {
    private static final Logger LOGGER = LogManager.getLogger(AbstractIndex.class);

    private String index;
    private String type;
    private Client client;
    private IdGenerator<E> idGenerator;

    public AbstractIndex(String index, String type, Client client, IdGenerator<E> idGenerator) {
        this.index = Objects.requireNonNull(index);
        this.type = Objects.requireNonNull(index);
        this.client = Objects.requireNonNull(client);
        this.idGenerator = Objects.requireNonNull(idGenerator);
    }

    @Override
    public void index(E element) {
        try {
            IndexRequest indexRequest = prepareIndexRequest(element);
            client.index(indexRequest).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            LOGGER.error("Error while executing index request", e);
        }
    }

    @Override
    public void index(List<? extends E> elements) {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (E element : elements) {
            try {
                IndexRequest indexRequest = prepareIndexRequest(element);
                bulkRequestBuilder.add(indexRequest);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }

        BulkResponse responses = bulkRequestBuilder.get();
        if (responses.hasFailures()) {
            Arrays.stream(responses.getItems())
                    .filter(BulkItemResponse::isFailed)
                    .forEach(resp -> {
                        LOGGER.error("Bulk indexing failed: {}, cause: {}", resp.getItemId(), resp.getFailureMessage());
                    });
        }
    }

    @Override
    public void delete(E element) {
        try {
            DeleteRequest deleteRequest = prepareDeleteRequest(element);
            client.delete(deleteRequest).get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error while executing delete request", e);
        }
    }

    @Override
    public void delete(List<? extends E> elements) {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (E element : elements) {
            DeleteRequest deleteRequest = prepareDeleteRequest(element);
            bulkRequestBuilder.add(deleteRequest);
        }

        BulkResponse responses = bulkRequestBuilder.get();
        if (responses.hasFailures()) {
            Arrays.stream(responses.getItems())
                    .filter(BulkItemResponse::isFailed)
                    .forEach(resp -> {
                        LOGGER.error("Bulk deleting failed: {}, cause: {}", resp.getItemId(), resp.getFailureMessage());
                    });
        }
    }

    private IndexRequest prepareIndexRequest(E element) throws IOException {
        IndexRequest indexRequest = Requests.indexRequest()
                .index(index)
                .type(type)
                .source(getSource(element), getContentType())
                .id(idGenerator.generate(element));
        return indexRequest;
    }

    private DeleteRequest prepareDeleteRequest(E element) {
        DeleteRequest deleteRequest = Requests.deleteRequest(index)
                .id(idGenerator.generate(element))
                .type(type);
        return deleteRequest;
    }

    protected abstract XContentType getContentType();

    protected abstract byte[] getSource(E element) throws IOException;
}
