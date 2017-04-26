package org.myas.victims.search.manager;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * Created by Mykhailo Yashchuk on 11.04.2017.
 */
public class ESAdminManager {
    private static final Logger LOGGER = LogManager.getLogger(ESAdminManager.class);

    private static final int DEFAULT_NUMBER_OF_SHARDS = 1;
    private static final int DEFAULT_NUMBER_OF_REPLICAS = 1;
    private static final XContentType DEFAULT_MAPPING_TYPE = XContentType.JSON;

    private int numberOfShards;
    private int numberOfReplicas;

    private Client client;

    public ESAdminManager(Client client) {
        this(DEFAULT_NUMBER_OF_SHARDS, DEFAULT_NUMBER_OF_REPLICAS, client);
    }

    public ESAdminManager(int numberOfShards, int numberOfReplicas, Client client) {
        this.client = Objects.requireNonNull(client);
        this.numberOfShards = (numberOfShards > 0) ? numberOfShards : DEFAULT_NUMBER_OF_SHARDS;
        this.numberOfReplicas = (numberOfReplicas > 0) ? numberOfReplicas : DEFAULT_NUMBER_OF_REPLICAS;
    }

    public String[] getIndices() {
        return client.admin().indices().prepareGetIndex().get().indices();
    }

    public void createIndex(String indexName, String type, String mapping) {
        CreateIndexResponse indexResponse = client.admin().indices().prepareCreate(indexName)
                .setSettings(Settings.builder()
                        .put("index.number_of_shards", numberOfShards)
                        .put("index.number_of_replicas", numberOfReplicas)
                )
                .addMapping(type, mapping, DEFAULT_MAPPING_TYPE)
                .get();

        if (indexResponse.isAcknowledged()) {
            LOGGER.info("Index {} has been successfully created", indexName);
        } else {
            LOGGER.error("Index {} failed to be created", indexName);
        }
    }
}
