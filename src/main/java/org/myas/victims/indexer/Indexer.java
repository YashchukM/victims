package org.myas.victims.indexer;

import static java.lang.Math.min;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.client.Client;
import org.myas.victims.core.analyzer.PageAnalyzer;
import org.myas.victims.core.domain.Region;
import org.myas.victims.core.domain.UnrecognizedRecord;
import org.myas.victims.core.domain.Victim;
import org.myas.victims.core.helper.IOHelper;
import org.myas.victims.search.index.Index;
import org.myas.victims.search.manager.ESAdminManager;
import org.myas.victims.search.manager.ESSearchManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Mykhailo Yashchuk on 12.04.2017.
 */
public class Indexer {
    private static final String APPLICATION_CONTEXT = "META-INF/spring/indexer-context.xml";
    private static final String VICTIMS_MAPPING = "org/myas/victims/indexer/victims_mapping.json";
    private static final String UNRECOGNIZED_MAPPING = "org/myas/victims/indexer/unrecognized_mapping.json";

    private ExecutorService executor;
    private int bulkSize;
    private long executionTimeout;
    private TimeUnit executionTimeUnit;

    private ESAdminManager esAdminManager;
    private Index<Victim> victimIndex;
    private Index<UnrecognizedRecord> unrecognizedIndex;

    public Indexer(int threadsNumber, int bulkSize, ESAdminManager esAdminManager) {
        this.bulkSize = bulkSize;
        this.esAdminManager = esAdminManager;
        this.executor = Executors.newFixedThreadPool(threadsNumber);
    }

    public Indexer(int threadsNumber, int bulkSize, long executionTimeout, TimeUnit executionTimeUnit, ESAdminManager
            esAdminManager) {
        this(threadsNumber, bulkSize, esAdminManager);
        this.executionTimeout = executionTimeout;
        this.executionTimeUnit = executionTimeUnit;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT);
        Indexer indexer = applicationContext.getBean(Indexer.class);

        Path docsPath = Paths.get(args[0]);
        int startPage = Integer.parseInt(args[1]);
        int endPage = Integer.parseInt(args[2]);
        Region region = Region.valueOf(args[3]);

        indexer.index(docsPath, startPage, endPage, region);
    }

    public void index(Path docsPath, int startDoc, int endDoc, Region region) throws InterruptedException, IOException {
        // TODO: create indices (where to place mappings)
        esAdminManager.createIndex(victimIndex.getName(),
                                   victimIndex.getType(),
                                   IOHelper.getResourceAsString(VICTIMS_MAPPING));
        esAdminManager.createIndex(unrecognizedIndex.getName(),
                                   unrecognizedIndex.getType(),
                                   IOHelper.getResourceAsString(UNRECOGNIZED_MAPPING));

        for (int startBulk = startDoc; startBulk < endDoc; startBulk += bulkSize) {
            int endBulk = min(startBulk + bulkSize, endDoc);
            PageAnalyzer pageAnalyzer = new PageAnalyzer(docsPath, region);
            executor.submit(new IndexerRunnable(
                    startBulk, endBulk, pageAnalyzer, victimIndex, unrecognizedIndex
            ));
        }
        executor.shutdown();
        executor.awaitTermination(executionTimeout, executionTimeUnit);
    }

    public void setVictimIndex(Index<Victim> victimIndex) {
        this.victimIndex = victimIndex;
    }

    public void setUnrecognizedIndex(Index<UnrecognizedRecord> unrecognizedIndex) {
        this.unrecognizedIndex = unrecognizedIndex;
    }
}
