package org.myas.victims.search.server;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Created by Mykhailo Yashchuk on 10.04.2017.
 */
public class RemoteESServer {
    private static final Logger LOGGER = LogManager.getLogger(RemoteESServer.class);

    private static final int DEFAULT_PORT = 9300;
    private static final String DEFAULT_CLUSTER_NAME = "victims-elastic";
    private static final String DEFAULT_ELASTICSEARCH_URL = "localhost:9300";

    private String elasticsearchUrl;
    private String clusterName;
    private Client client;

    public RemoteESServer() {
        this(DEFAULT_ELASTICSEARCH_URL, DEFAULT_CLUSTER_NAME);
    }

    public RemoteESServer(String elasticsearchUrl, String clusterName) {
        this.elasticsearchUrl = elasticsearchUrl;
        this.clusterName = clusterName;
    }

    public void initialize() {
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", false)
                .build();

        TransportClient transportClient = new PreBuiltTransportClient(settings);

        for (String url : elasticsearchUrl.split(",")) {
            String[] urlParts = url.split(":");

            InetSocketAddress address = new InetSocketAddress(
                    urlParts[0],
                    urlParts.length > 1 ? Integer.parseInt(urlParts[1]) : DEFAULT_PORT);
            address.getAddress();

            transportClient.addTransportAddress(new InetSocketTransportAddress(address));
        }

        client = transportClient;
        LOGGER.info("Remote ElasticSearch server started");
    }

    public Client getClient() {
        return client;
    }

    public void shutdown() {
        if (client != null) {
            client.close();
        }
        LOGGER.info("Elasticsearch cluster shut down");
    }
}
