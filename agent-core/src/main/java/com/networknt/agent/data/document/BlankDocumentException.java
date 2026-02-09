package com.networknt.agent.data.document;

import com.networknt.agent.exception.LangChain4jException;

public class BlankDocumentException extends LangChain4jException {

    public BlankDocumentException() {
        super("The document is blank");
    }
}
