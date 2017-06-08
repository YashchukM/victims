package org.myas.victims.core.helper;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

/**
 * Created by Mykhailo Yashchuk on 11.03.2017.
 */
public final class IOHelper {
    public static final String IMG_DIR = "images";
    public static final String PDF_DIR = "parts";
    public static final String TXT_DIR = "texts";

    public static final String IMG_PATTERN = "image-%s.png";
    public static final String PDF_PATTERN = "part-%s.pdf";
    public static final String TXT_PATTERN = "text-%s.txt";
    public static final String JSON_PATTERN = "victims-%s.json";

    public static final String RANGE_PATTERN = "%s_%s";

    private IOHelper() {}

    public static OutputStream getFileOutputStream(Path directory, String subPath, String fileName) throws IOException {
        Path childPath = directory.resolve(Paths.get(subPath));
        if (Files.notExists(childPath)) {
            Files.createDirectory(childPath);
        }
        return Files.newOutputStream(Paths.get(childPath.toString(), fileName));
    }

    public static InputStream getFileInputStream(Path directory, String subPath, String fileName) throws IOException {
        Path path = Paths.get(directory.toString(), subPath, fileName);
        return Files.newInputStream(path);
    }

    public static OutputStream getFileOutputStream(Path directory, String fileName) throws IOException {
        return getFileOutputStream(directory, "", fileName);
    }

    public static InputStream getFileInputStream(Path directory, String fileName) throws IOException {
        return getFileInputStream(directory, "", fileName);
    }

    public static ByteArrayInputStream toInputStream(ByteArrayOutputStream outputStream) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        outputStream.reset();
        return inputStream;
    }

    public static String getResourceAsString(String name) throws IOException {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
            if (inputStream == null) {
                throw new FileNotFoundException(name);
            }
            return IOUtils.toString(inputStream, UTF_8);
        }
    }
}
