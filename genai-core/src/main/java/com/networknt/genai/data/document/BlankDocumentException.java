package com.networknt.genai.data.document;

import com.networknt.genai.exception.LangChain4jException;

/**
 * Thrown when a document is blank.
 */
public class BlankDocumentException extends LangChain4jException {

    /**
     * Creates a new blank document exception.
     */
    public BlankDocumentException() {
        super("The document is blank");
    }
}
