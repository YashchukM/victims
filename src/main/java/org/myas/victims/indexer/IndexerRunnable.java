package org.myas.victims.indexer;

import static java.lang.String.format;

import static org.myas.victims.core.helper.IOHelper.JSON_PATTERN;
import static org.myas.victims.core.helper.IOHelper.RANGE_PATTERN;
import static org.myas.victims.core.helper.IOHelper.getFileInputStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myas.victims.core.analyzer.PageAnalyzer;
import org.myas.victims.core.domain.UnrecognizedRecord;
import org.myas.victims.core.domain.Victim;
import org.myas.victims.search.index.Index;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Mykhailo Yashchuk on 12.04.2017.
 */
public class IndexerRunnable implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(IndexerRunnable.class);

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
            indexVictims(pageAnalyzer.getAnalyzeDir());
            indexUnrecognizedRecords(pageAnalyzer.getUnrecognizedDir());
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private void indexVictims(Path analyzePath) throws IOException {
        for (int doc = startDocument; doc <= endDocument; doc++) {
            List<Victim> victims = objectMapper.readValue(
                    getFileInputStream(analyzePath, format(JSON_PATTERN, doc)),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Victim.class)
            );
            victimIndex.index(victims);
        }
    }

    private void indexUnrecognizedRecords(Path unrecognizedPath) throws IOException {
        String range = format(RANGE_PATTERN, startDocument, endDocument);
        List<UnrecognizedRecord> unrecognizedRecords = objectMapper.readValue(
                getFileInputStream(unrecognizedPath, format(JSON_PATTERN, range)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, UnrecognizedRecord.class)
        );
        unrecognizedIndex.index(unrecognizedRecords);
    }
}
