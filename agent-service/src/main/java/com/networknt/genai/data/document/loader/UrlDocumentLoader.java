package com.networknt.genai.data.document.loader;

import com.networknt.genai.data.document.Document;
import com.networknt.genai.data.document.DocumentLoader;
import com.networknt.genai.data.document.DocumentParser;
import com.networknt.genai.data.document.source.UrlSource;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlDocumentLoader {

    /**
     * Loads a document from the specified URL.
     *
     * @param url            The URL of the file.
     * @param documentParser The parser to be used for parsing text from the URL.
     * @return document
     */
    public static Document load(URL url, DocumentParser documentParser) {
        return DocumentLoader.load(UrlSource.from(url), documentParser);
    }

    /**
     * Loads a document from the specified URL.
     *
     * @param url            The URL of the file.
     * @param documentParser The parser to be used for parsing text from the URL.
     * @return document
     * @throws RuntimeException If specified URL is malformed.
     */
    public static Document load(String url, DocumentParser documentParser) {
        return load(createUrl(url), documentParser);
    }

    /**
     * Creates a URL from the specified string.
     *
     * @param url The URL string.
     * @return the URL
     * @throws IllegalArgumentException If specified URL is malformed.
     */
    static URL createUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
