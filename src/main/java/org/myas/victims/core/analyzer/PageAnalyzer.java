package org.myas.victims.core.analyzer;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import static org.myas.victims.core.extractor.TesseractExtractor.TXT_PATTERN;
import static org.myas.victims.core.helper.IOHelper.getFileInputStream;
import static org.myas.victims.core.helper.IOHelper.getFileOutputStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Mykhailo Yashchuk on 11.03.2017.
 */
public class PageAnalyzer {
    private static final Logger LOGGER = LogManager.getLogger(PageAnalyzer.class);
    private static final String ANALYZE_DIR = "analyzed";

    protected Path textsPath;
    protected Path analyzeDirectory;

    public PageAnalyzer(Path textsPath) {
        this.textsPath = textsPath;
        this.analyzeDirectory = textsPath.resolveSibling(ANALYZE_DIR);
    }

    public void analyze(int startPage, int endPage) {
        LOGGER.info("Start analyzing texts from dir {} pages {}-{}", textsPath, startPage, endPage);

        for (int page = startPage; page <= endPage; page++) {
            try (InputStream is = getFileInputStream(textsPath, "", format(TXT_PATTERN, page));
                 OutputStream os = getFileOutputStream(analyzeDirectory, "", format(TXT_PATTERN, page))) {
                String pageContent = IOUtils.toString(is, UTF_8);
                analyze(pageContent, page);
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
    }

    protected void analyze(String pageContent, int page) {
        String[] records = pageContent.split("\n\n");

        if (isEvenPage(page)) {

        } else {

        }
    }

    private boolean isEvenPage(int page) {
        return page % 2 == 0;
    }
}
