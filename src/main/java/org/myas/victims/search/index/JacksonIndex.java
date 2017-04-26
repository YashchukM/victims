package org.myas.victims.search.index;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.myas.victims.search.index.id.IdGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Mykhailo Yashchuk on 11.04.2017.
 */
public class JacksonIndex<E> extends AbstractIndex<E> {
    private ObjectMapper objectMapper;

    public JacksonIndex(String index, String type, Client client, IdGenerator<E> idGenerator, ObjectMapper mapper) {
        super(index, type, client, idGenerator);
        this.objectMapper = mapper;
    }

    @Override
    protected XContentType getContentType() {
        return XContentType.JSON;
    }

    @Override
    protected byte[] getSource(E element) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(element);
    }
}
