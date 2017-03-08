package org.myas.victims.core;

import static java.lang.String.format;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/**
 * Created by Mykhailo Yashchuk on 05.03.2017.
 */
public class TesseractExtractor implements RecordExtractor {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static final int DEFAULT_DPI = 300;
    private static final String DEFAULT_TESSERACT_LANGUAGE = "ukr";

    private static final String IMG_DIR = "images";
    private static final String PDF_DIR = "parts";
    private static final String TXT_DIR = "texts";

    private static final String IMG_PATTERN = "image-%s.png";
    private static final String PDF_PATTERN = "part-%s.pdf";
    private static final String TXT_PATTERN = "text-%s.txt";

    private static final String IMG_FORMAT = "png";

    private int imageDpi;
    private String tesseractLanguage;
    private Parser tesseractParser;
    private ParseContext tesseractParseContext;

    private Path extractDirectory;

    private PDDocument document;
    private Path extractedFilePath;
    private int batchSize;

    public TesseractExtractor(Path extractedFilePath) {
        this(extractedFilePath, 1);
    }

    public TesseractExtractor(Path extractedFilePath, int batchSize) {
        this.extractedFilePath = Objects.requireNonNull(extractedFilePath);
        this.batchSize = batchSize;

        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be > 0");
        }
        if (Files.notExists(extractedFilePath)) {
            throw new IllegalArgumentException("Unexisting path: " + extractedFilePath);
        }

        this.imageDpi = DEFAULT_DPI;
        this.tesseractLanguage = DEFAULT_TESSERACT_LANGUAGE;
        this.extractDirectory = extractedFilePath.normalize().getParent();
    }

    @Override
    public void extract(int startPage, int endPage) throws IOException {
        LOGGER.info("Start extracting from file {} pages {}-{}", extractedFilePath, startPage, endPage);

        try {
            initializeParsing();
            for (int page = startPage; page <= endPage; page++) {
                try {
                    extractPdf(page);
                    convertToImage(page, imageDpi);
                    extractText(page);
                } catch (IOException | SAXException | TikaException e) {
                    LOGGER.error(e);
                }
            }
        } finally {
            document.close();
        }
    }

    private void initializeParsing() throws IOException {
        document = PDDocument.load(Files.newInputStream(extractedFilePath));
        tesseractParser = new AutoDetectParser();

        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setLanguage(tesseractLanguage);

        tesseractParseContext = new ParseContext();
        tesseractParseContext.set(TesseractOCRConfig.class, config);
    }

    private void extractText(int page) throws IOException, TikaException, SAXException {
        LOGGER.info("Start recognizing page {}", page);

        try (InputStream inputStream = getFileInputStream(IMG_DIR, format(IMG_PATTERN, page));
             OutputStream outputStream = getFileOutputStream(TXT_DIR, format(TXT_PATTERN, page))) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            tesseractParser.parse(inputStream, handler, metadata, tesseractParseContext);
            outputStream.write(handler.toString().getBytes(StandardCharsets.UTF_8));
        }

        LOGGER.info("Finish recognizing page {}", page);
    }

    private void convertToImage(int page, int dpi) throws IOException {
        LOGGER.info("Start converting page {} to image", page);

        try (InputStream inputStream = getFileInputStream(PDF_DIR, format(PDF_PATTERN, page));
             OutputStream outputStream = getFileOutputStream(IMG_DIR, format(IMG_PATTERN, page));
             PDDocument document = PDDocument.load(inputStream)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bim = renderer.renderImageWithDPI(0, dpi, ImageType.RGB);
            ImageIOUtil.writeImage(bim, IMG_FORMAT, outputStream, dpi);
        }

        LOGGER.info("Finish converting page {} to image", page);
    }

    private void extractPdf(int page) throws IOException {
        LOGGER.info("Start extracting page {}", page);

        try (OutputStream outputStream = getFileOutputStream(PDF_DIR, format(PDF_PATTERN, page));
             PDDocument result = new PDDocument()) {
            result.addPage(document.getPage(page - 1));
            result.save(outputStream);
        }

        LOGGER.info("Finish extracting page {}", page);
    }

    private OutputStream getFileOutputStream(String subPath, String fileName) throws IOException {
        Path childPath = extractDirectory.resolve(Paths.get(subPath));
        if (Files.notExists(childPath)) {
            Files.createDirectory(childPath);
        }
        return Files.newOutputStream(Paths.get(childPath.toString(), fileName));
    }

    private InputStream getFileInputStream(String subPath, String fileName) throws IOException {
        Path path = Paths.get(extractDirectory.toString(), subPath, fileName);
        return Files.newInputStream(path);
    }

    public void setImageDpi(int imageDpi) {
        this.imageDpi = imageDpi;
    }

    public void setTesseractLanguage(String tesseractLanguage) {
        this.tesseractLanguage = tesseractLanguage;
    }

    public void setExtractDirectory(Path extractDirectory) {
        this.extractDirectory = extractDirectory;
    }
}
