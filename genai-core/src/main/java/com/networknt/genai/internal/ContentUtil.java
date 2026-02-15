package com.networknt.genai.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * Utility class for content operations.
 */
public class ContentUtil {

    private ContentUtil() { }

    /**
     * Extracts base64 content from a file.
     *
     * @param filePath the file path
     * @return the base64 content
     */
    public static String extractBase64Content(Path filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(filePath);
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
