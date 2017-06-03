package org.myas.victims.web.config;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.Client;
import org.myas.victims.search.manager.ESSearchManager;
import org.myas.victims.search.server.RemoteESServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Mykhailo Yashchuk on 23.05.2017.
 */
@Configuration
public class CommonBeansConfig {
    private static final String DEFAULT_ES_URL = "54.201.61.253";
    private static final String DEFAULT_ES_CLUSTER_NAME = "victims-elastic";

    private ObjectMapper objectMapper;
    private Client client;
    private RemoteESServer esServer;
    private ESSearchManager esSearchManager;

    @PostConstruct
    public void init() {
        this.esServer = new RemoteESServer(DEFAULT_ES_URL, DEFAULT_ES_CLUSTER_NAME);
        this.esServer.initialize();

        this.client = esServer.getClient();
        this.objectMapper = new ObjectMapper();

        this.esSearchManager = new ESSearchManager(client, objectMapper);
    }

    @Bean
    public ESSearchManager esSearchManager() {
        return esSearchManager;
    }
}
