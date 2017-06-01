package org.myas.victims.search.manager;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.myas.victims.core.domain.Victim;

/**
 * Created by Mykhailo Yashchuk on 29.05.2017.
 */
public class ESSearchManager {
    private static final Logger LOGGER = LogManager.getLogger(ESSearchManager.class);
    private static final int DEFAULT_SIZE = 15;

    private Client client;

    public ESSearchManager(Client client) {
        this.client = Objects.requireNonNull(client);
    }

    public List<Victim> searchVictims(String village, String district, String name) {
        SearchResponse response = client.prepareSearch("victims")
                .setTypes("victim")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQuery()
                        .must(termQuery("village", village))
                        .must(termQuery("district", district)))                 // Query
                .setFrom(0).setSize(DEFAULT_SIZE)
                .get();

        List<Victim> victims = new ArrayList<>();
        for (SearchHit hit : response.getHits().hits()) {
            System.out.println(hit.getSourceAsString());
        }
        return victims;
    }
}
