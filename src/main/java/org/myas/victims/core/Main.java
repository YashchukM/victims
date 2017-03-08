package org.myas.victims.core;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 * Created by Mykhailo Yashchuk on 28.02.2017.
 */
public class Main {
    private static final String VICTIMS_FILE = "holodomor.pdf";
    private static final String RESOURCES_DIR = "D:\\ProjectsIDEA\\victims\\src\\main\\resources";

    // 60 - 823
    public static void main(String[] args) throws IOException, TikaException, SAXException {
        Path path = Paths.get(RESOURCES_DIR, VICTIMS_FILE);
        TesseractExtractor extractor = new TesseractExtractor(path);
        extractor.extract(701, 823);
    }
}
