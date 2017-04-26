package org.myas.victims.indexer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private int threadsNumber;
    private int bulkSize;

    private ESAdminManager esAdminManager;
    private Index<Victim> victimIndex;
    private Index<UnrecognizedRecord> unrecognizedIndex;

    public Indexer(int threadsNumber, int bulkSize, ESAdminManager esAdminManager) {
        this.threadsNumber = threadsNumber;
        this.bulkSize = bulkSize;
        this.esAdminManager = esAdminManager;

        this.executor = Executors.newFixedThreadPool(threadsNumber);
    }

    public void index(Path docsPath, int startDoc, int endDoc) {

    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT);
        Indexer indexer = applicationContext.getBean(Indexer.class);

        Path docsPath = Paths.get(args[0]);
        int startPage = Integer.parseInt(args[1]);
        int endPage = Integer.parseInt(args[2]);

        indexer.index(docsPath, startPage, endPage);
    }
}
