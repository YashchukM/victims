package org.myas.victims.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/**
 * Created by Mykhailo Yashchuk on 28.02.2017.
 */
public class Main {
    public static void main(String[] args) throws IOException, TikaException, SAXException {
        testParse();
    }

    private static void testParse() throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputStream = new FileInputStream(new File("holodomor.pdf"));
        ParseContext parseContext = new ParseContext();

        PDFParser parser = new PDFParser();
        parser.parse(inputStream, handler, metadata, parseContext);

        for (String name: metadata.names()) {
            System.out.println(name + ": " + metadata.get(name));
        }
    }
}
