package hu.unideb.inf.timetableGenerator.domain.runner.utils;

import hu.unideb.inf.timetableGenerator.domain.runner.MainJSONFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

/**
 * Utility class for parsing files from the resources folder.
 */
public final class ResourceFileParser {

    /**
     * Returns the contents of a file placed in the resources folder.
     * @param fileName the name of the file to parse. Should be in resources folder.
     * @return the contents of the file as a String
     */
    public static String parseResourceFile(String fileName) {
        URL resourceUrl = MainJSONFile.class.getClassLoader().getResource(fileName);

        if (resourceUrl == null) {
            throw new RuntimeException("Resource file not found: " + fileName);
        }

        File file;
        try {
            file = new File(resourceUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
