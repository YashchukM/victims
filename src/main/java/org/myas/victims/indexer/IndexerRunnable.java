package org.myas.victims.indexer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.myas.victims.core.analyzer.PageAnalyzer;
import org.myas.victims.core.domain.UnrecognizedRecord;
import org.myas.victims.core.domain.Victim;
import org.myas.victims.search.index.Index;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Mykhailo Yashchuk on 12.04.2017.
 */
public class IndexerRunnable implements Runnable {
    private int startDocument;
    private int endDocument;

    private PageAnalyzer pageAnalyzer;

    private Index<Victim> victimIndex;
    private Index<UnrecognizedRecord> unrecognizedIndex;

    private ObjectMapper objectMapper;

    public IndexerRunnable(int startDocument, int endDocument, PageAnalyzer pageAnalyzer,
                           Index<Victim> victimIndex, Index<UnrecognizedRecord> unrecognizedIndex) {
        this.startDocument = startDocument;
        this.endDocument = endDocument;
        this.pageAnalyzer = pageAnalyzer;
        this.victimIndex = victimIndex;
        this.unrecognizedIndex = unrecognizedIndex;

        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            pageAnalyzer.analyze(startDocument, endDocument);
            Path analyzePath = pageAnalyzer.getAnalyzeDir();
            Path unrecognizedPath = pageAnalyzer.getUnrecognizedDir();

            for (int doc = startDocument; doc <= endDocument; doc++) {
                List<Victim> victims = objectMapper.readValue(
                        pageAnalyzer.getAnalyzeDir().toFile(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Victim.class)
                );
                victimIndex.index(victims);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
