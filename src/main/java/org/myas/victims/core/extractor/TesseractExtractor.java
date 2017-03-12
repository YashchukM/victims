package org.myas.victims.core.extractor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
public abstract class TesseractExtractor implements RecordExtractor {
    private static final Logger LOGGER = LogManager.getLogger(TesseractExtractor.class);

    private static final int DEFAULT_DPI = 300;
    private static final String DEFAULT_TESSERACT_LANGUAGE = "ukr";

    protected static final String IMG_DIR = "images";
    protected static final String PDF_DIR = "parts";
    public static final String TXT_DIR = "texts";

    protected static final String IMG_PATTERN = "image-%s.png";
    protected static final String PDF_PATTERN = "part-%s.pdf";
    public static final String TXT_PATTERN = "text-%s.txt";

    protected static final String IMG_FORMAT = "png";

    protected int imageDpi;
    protected String tesseractLanguage;
    protected Parser tesseractParser;
    protected ParseContext tesseractParseContext;

    protected Path extractDirectory;

    protected PDDocument document;
    protected Path extractedFilePath;

    public TesseractExtractor(Path extractedFilePath) {
        this.extractedFilePath = Objects.requireNonNull(extractedFilePath);
        if (Files.notExists(extractedFilePath)) {
            throw new IllegalArgumentException("Unexisting path: " + extractedFilePath);
        }

        this.imageDpi = DEFAULT_DPI;
        this.tesseractLanguage = DEFAULT_TESSERACT_LANGUAGE;
        this.extractDirectory = extractedFilePath.normalize().getParent();
    }

    protected void initializeParsing() throws IOException {
        document = PDDocument.load(Files.newInputStream(extractedFilePath));
        tesseractParser = new AutoDetectParser();

        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setLanguage(tesseractLanguage);

        tesseractParseContext = new ParseContext();
        tesseractParseContext.set(TesseractOCRConfig.class, config);
    }

    protected void extractText(InputStream inputStream, OutputStream outputStream, int page)
            throws IOException, TikaException, SAXException {
        LOGGER.info("Start recognizing page {}", page);

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        tesseractParser.parse(inputStream, handler, metadata, tesseractParseContext);
        outputStream.write(handler.toString().getBytes(StandardCharsets.UTF_8));

        LOGGER.info("Finish recognizing page {}", page);
    }

    protected void convertToImage(InputStream inputStream, OutputStream outputStream, int page, int dpi)
            throws IOException {
        LOGGER.info("Start converting page {} to image", page);

        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage bim = renderer.renderImageWithDPI(0, dpi, ImageType.RGB);
            ImageIOUtil.writeImage(bim, IMG_FORMAT, outputStream, dpi);
        }

        LOGGER.info("Finish converting page {} to image", page);
    }

    protected void extractPdf(OutputStream outputStream, int page) throws IOException {
        LOGGER.info("Start extracting page {}", page);

        try (PDDocument result = new PDDocument()) {
            result.addPage(document.getPage(page - 1));
            result.save(outputStream);
        }

        LOGGER.info("Finish extracting page {}", page);
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
