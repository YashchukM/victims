package org.myas.victims.indexer;

import static java.lang.Math.min;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.myas.victims.core.analyzer.PageAnalyzer;
import org.myas.victims.core.domain.Region;
import org.myas.victims.core.domain.UnrecognizedRecord;
import org.myas.victims.core.domain.Victim;
import org.myas.victims.search.index.Index;
import org.myas.victims.search.manager.ESAdminManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Mykhailo Yashchuk on 12.04.2017.
 */
public class Indexer {
    private static final String APPLICATION_CONTEXT = "META-INF/spring/indexer-context.xml";

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

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT);
        Indexer indexer = applicationContext.getBean(Indexer.class);

        Path docsPath = Paths.get(args[0]);
        int startPage = Integer.parseInt(args[1]);
        int endPage = Integer.parseInt(args[2]);

        indexer.index(docsPath, startPage, endPage);
    }

    public void index(Path docsPath, int startDoc, int endDoc) throws InterruptedException {
        // TODO: create indices (where to place mappings)

        for (int startBulk = startDoc; startBulk < endDoc; startBulk += bulkSize) {
            int endBulk = min(startBulk + bulkSize, endDoc);
            // TODO: add region as class member, argument
            PageAnalyzer pageAnalyzer = new PageAnalyzer(docsPath, Region.VINNYTSYA);
            executor.submit(new IndexerRunnable(
                    startBulk, endBulk, pageAnalyzer, victimIndex, unrecognizedIndex
            ));
        }
        executor.awaitTermination(executionTimeout, executionTimeUnit);
    }

    public void setVictimIndex(Index<Victim> victimIndex) {
        this.victimIndex = victimIndex;
    }

    public void setUnrecognizedIndex(Index<UnrecognizedRecord> unrecognizedIndex) {
        this.unrecognizedIndex = unrecognizedIndex;
    }
}
