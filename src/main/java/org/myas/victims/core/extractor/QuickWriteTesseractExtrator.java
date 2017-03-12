package org.myas.victims.core.extractor;

import static java.lang.String.format;

import static org.myas.victims.core.helper.IOHelper.getFileOutputStream;
import static org.myas.victims.core.helper.IOHelper.toInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Mykhailo Yashchuk on 10.03.2017.
 */
public class QuickWriteTesseractExtrator extends TesseractExtractor {
    private static final Logger LOGGER = LogManager.getLogger(QuickWriteTesseractExtrator.class);

    public QuickWriteTesseractExtrator(Path extractedFilePath) {
        super(extractedFilePath);
    }

    @Override
    public void extract(int startPage, int endPage) throws IOException {
        LOGGER.info("Start extracting from file {} pages {}-{}", extractedFilePath, startPage, endPage);
        try {
            initializeParsing();
            for (int page = startPage; page <= endPage; page++) {
                try (OutputStream txtOS = getFileOutputStream(extractDirectory, TXT_DIR, format(TXT_PATTERN, page));
                     ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    extractPdf(os, page);
                    convertToImage(toInputStream(os), os, page, imageDpi);
                    extractText(toInputStream(os), txtOS, page);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        } finally {
            document.close();
        }
    }
}
