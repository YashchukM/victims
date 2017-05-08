package org.myas.victims.core.extractor;

import static java.lang.Math.min;
import static java.lang.String.format;

import static org.myas.victims.core.helper.IOHelper.TXT_DIR;
import static org.myas.victims.core.helper.IOHelper.TXT_PATTERN;
import static org.myas.victims.core.helper.IOHelper.getFileOutputStream;
import static org.myas.victims.core.helper.IOHelper.toInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Mykhailo Yashchuk on 10.03.2017.
 */
public class InMemTesseractExtractor extends TesseractExtractor {
    private static final Logger LOGGER = LogManager.getLogger(InMemTesseractExtractor.class);

    public static final String PAGE_SEPARATOR = "\n__new_page__\n";
    public static final int DEFAULT_BATCH_SIZE = 10;

    private int filesInBatch;

    public InMemTesseractExtractor(Path extractedFilePath) {
        this(extractedFilePath, DEFAULT_BATCH_SIZE);
    }

    public InMemTesseractExtractor(Path extractedFilePath, int filesInBatch) {
        super(extractedFilePath);
        this.filesInBatch = filesInBatch;
    }

    @Override
    public void extract(int startPage, int endPage) throws IOException {
        LOGGER.info("Start extracting from file {} pages {}-{}", extractedFilePath, startPage, endPage);
        try {
            initializeParsing();
            ByteArrayOutputStream pageOS = new ByteArrayOutputStream();
            ByteArrayOutputStream batchOS = new ByteArrayOutputStream();

            for (int sbPage = startPage; sbPage <= endPage; sbPage += filesInBatch) {
                for (int bPage = sbPage; (bPage < sbPage + filesInBatch) && (bPage <= endPage); bPage++) {
                    try {
                        extractPdf(pageOS, bPage);
                        convertToImage(toInputStream(pageOS), pageOS, bPage);
                        extractText(toInputStream(pageOS), batchOS, bPage);
                    } catch (Exception e) {
                        LOGGER.error(e);
                    }
                    batchOS.write(PAGE_SEPARATOR.getBytes(StandardCharsets.UTF_8));
                }

                String fileName = format(TXT_PATTERN, format("%s_%s", sbPage, min(endPage, sbPage + filesInBatch)));
                try (OutputStream outputStream = getFileOutputStream(extractDirectory, TXT_DIR, fileName)) {
                    batchOS.writeTo(outputStream);
                    batchOS.reset();
                }
            }
        } finally {
            document.close();
        }
    }
}
