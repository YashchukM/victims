package org.myas.victims.search.manager;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.myas.victims.core.domain.Victim;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Mykhailo Yashchuk on 29.05.2017.
 */
public class ESSearchManager {
    private static final Logger LOGGER = LogManager.getLogger(ESSearchManager.class);
    private static final int DEFAULT_SIZE = 15;

    private Client client;
    private ObjectMapper objectMapper;

    public ESSearchManager(Client client, ObjectMapper objectMapper) {
        this.client = Objects.requireNonNull(client);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public List<Victim> searchVictims(String village, String district, String name) {
        SearchResponse response = client.prepareSearch("victims")
                .setTypes("victim")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(victimsSearchQuery(name, village, district))
                .setFrom(0).setSize(DEFAULT_SIZE)
                .get();

        List<Victim> victims = new ArrayList<>();
        try {
            for (SearchHit hit : response.getHits().hits()) {
                victims.add(objectMapper.readValue(hit.getSourceAsString(), Victim.class));
            }
        } catch (IOException e) {
            LOGGER.error("ES error", e);
        }
        return victims;
    }

    private BoolQueryBuilder victimsSearchQuery(String name, String village, String district) {
        BoolQueryBuilder qb = boolQuery();
        if (!StringUtils.isEmpty(village)) qb.must(termQuery("village", village));
        if (!StringUtils.isEmpty(district)) qb.must(termQuery("district", district));
        if (!StringUtils.isEmpty(name)) qb.must(matchQuery("name", name));
        return  qb;
    }
}
