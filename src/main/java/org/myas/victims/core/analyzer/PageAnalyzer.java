package org.myas.victims.core.analyzer;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.myas.victims.core.extractor.TesseractExtractor.TXT_PATTERN;
import static org.myas.victims.core.helper.IOHelper.getFileInputStream;
import static org.myas.victims.core.helper.IOHelper.getFileOutputStream;
import static org.myas.victims.core.helper.Patterns.DISTRICT_PATTERN;
import static org.myas.victims.core.helper.Patterns.NAMED_RECORD_PATTERN;
import static org.myas.victims.core.helper.Patterns.ODD_PAGE_HEADER_PATTERN;
import static org.myas.victims.core.helper.Patterns.SPLIT_RECORD_PATTERN;
import static org.myas.victims.core.helper.Patterns.VILLAGE_PATTERN;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.myas.victims.core.domain.Region;
import org.myas.victims.core.domain.UnrecognizedRecord;
import org.myas.victims.core.domain.Victim;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Mykhailo Yashchuk on 11.03.2017.
 */
public class PageAnalyzer {
    private static final Logger LOGGER = LogManager.getLogger(PageAnalyzer.class);
    private static final String ANALYZE_DIR = "analyzed";
    private static final String UNRECOGNIZED_DIR = "unrecognized";
    private static final String JSON_PATTERN = "text-%s.json";

    private static final String RECORD_SEPARATOR = "\n\n";
    private static final String RANGE_SEPARATOR = "_";

    private static final String DEFAULT_DISTRICT = "DD";
    private static final String DEFAULT_VILLAGE = "DV";
    private static final String DEFAULT_RECORD = "DR";

    protected Path textsDir;
    protected Path analyzeDir;
    protected Path unrecognizedDir;

    private String lastRecord;
    private String lastVillage;
    private String lastDistrict;
    private Victim lastVictim;

    private Region region;

    public PageAnalyzer(Path textsDir) {
        this.textsDir = textsDir;
        this.analyzeDir = textsDir.resolveSibling(ANALYZE_DIR);
        this.unrecognizedDir = textsDir.resolveSibling(UNRECOGNIZED_DIR);

        this.lastRecord = DEFAULT_RECORD;
        this.lastVillage = DEFAULT_VILLAGE;
        this.lastDistrict = DEFAULT_DISTRICT;
    }

    public PageAnalyzer(Path textsDir, Region region) {
        this.textsDir = textsDir;
        this.region = region;
    }

    public void analyze(int startPage, int endPage) throws IOException {
        LOGGER.info("Start analyzing texts from dir {} pages {}-{}", textsDir, startPage, endPage);

        ObjectMapper objectMapper = new ObjectMapper();
        List<UnrecognizedRecord> unrecognized = new ArrayList<>();
        for (int page = startPage; page <= endPage; page++) {
            try (InputStream is = getFileInputStream(textsDir, "", format(TXT_PATTERN, page));
                 OutputStream os = getFileOutputStream(analyzeDir, "", format(JSON_PATTERN, page))) {
                String pageContent = IOUtils.toString(is, UTF_8);
                List<Victim> victims = analyze(pageContent, page, unrecognized);
                objectMapper.writeValue(os, victims);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }

        String range = "" + startPage + RANGE_SEPARATOR + endPage;
        try (OutputStream os = getFileOutputStream(unrecognizedDir, "", format(JSON_PATTERN, range))) {
            objectMapper.writeValue(os, unrecognized);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    protected List<Victim> analyze(String pageContent, int page, List<UnrecognizedRecord> unrecognized) {
        String[] records = pageContent.split(RECORD_SEPARATOR);
        List<Victim> victims = new ArrayList<>();

        for (int i = 0; i < records.length; i++) {
            if (String.valueOf(page).equals(records[i])) continue;  // Parsed page number
            if ((i < 3) && isEvenPage(page)) continue;              // First 3 records of even page are trash
            if (!isEvenPage(page) && ODD_PAGE_HEADER_PATTERN.matcher(records[i]).matches()) continue;

            String record = inlineRecord(records[i]);
            if (DISTRICT_PATTERN.matcher(record).matches()) {
                updateDistrict(record);
            } else if (VILLAGE_PATTERN.matcher(record).matches()) {
                updateVillage(record);
            } else if (NAMED_RECORD_PATTERN.matcher(record).matches() || !DEFAULT_RECORD.equals(lastRecord)) {
                updateRecord(record, page, victims);
            } else {
                updateUnrecognized(record, page, unrecognized);
            }
        }
        resetDistrict(victims);

        return victims;
    }

    private void resetDistrict(List<Victim> victims) {
        victims.forEach(victim -> victim.setDistrict(lastDistrict));
    }

    private void updateVillage(String record) {
        lastVillage = record.substring(record.indexOf(".") + 1).toLowerCase();
    }

    private void updateDistrict(String record) {
        lastDistrict = record.split(" ")[0].trim().toLowerCase();
    }

    private void updateRecord(String record, int page, List<Victim> victims) {
        if (SPLIT_RECORD_PATTERN.matcher(record).matches()) {           // Previous record was unfinished
            if (DEFAULT_RECORD.equals(lastRecord)) {                    // First part of split record
                lastRecord = record.substring(0, record.length() - 1);
            } else {                                                    // Next part, again split
                lastRecord += record.substring(0, record.length() - 1);
            }
        } else {
            if (!DEFAULT_RECORD.equals(lastRecord)) {                   // End of split record
                record = inlineRecord(lastRecord + record);
            }

            Victim victim = new Victim();
            victim.setFullRecord(record);
            victim.setPageNumber(page);
            victim.setVillage(lastVillage);
            victim.setDistrict(lastDistrict);
            victim.setName(record.split(",")[0]);
            victims.add(victim);

            lastVictim = victim;
            lastRecord = DEFAULT_RECORD;
        }
    }

    private void updateUnrecognized(String record, int page, List<UnrecognizedRecord> unrecognized) {
        UnrecognizedRecord uRecord = new UnrecognizedRecord();
        uRecord.setVictim(lastVictim);
        uRecord.setRecord(record);
        uRecord.setPage(page);
        unrecognized.add(uRecord);
    }

    /**
     * Remove endline symbols from record to make it one-line
     */
    private String inlineRecord(String record) {
        String result = record
                // one word
                .replaceAll("(?<b>[а-яА-ЯЯїЇйЙіІ])-\\n(?<a>[а-яА-ЯЯїЇйЙіІ0-9])", "${b}${a}")
                // number ranges
                .replaceAll("(?<b>[0-9])[ ]*-\\n[ ]*(?<a>[0-9])", "${b}${a}")
                // two words
                .replaceAll("(?<b>[а-яА-ЯїЇйЙіІ0-9,.-:])[ ]*\\n[ ]*(?<a>[а-яА-ЯЯїЇйЙіІ0-9/(])", "${b} ${a}")
                // all others
                .replace('\n', ' ');
        return result;
    }

    private boolean isEvenPage(int page) {
        return page % 2 == 0;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
