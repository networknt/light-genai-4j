package com.networknt.genai.spi.data.document.parser;

import com.networknt.genai.Internal;
import com.networknt.genai.data.document.DocumentParser;

/**
 * A factory for creating {@link DocumentParser} instances through SPI.
 * <br>
 * Available implementations: {@code ApacheTikaDocumentParserFactory}
 * in the {@code langchain4j-document-parser-apache-tika} module.
 * For the "Easy RAG", import {@code langchain4j-easy-rag} module.
 */
@Internal
public interface DocumentParserFactory {

    /**
     * Creates a new {@link DocumentParser}.
     *
     * @return the document parser
     */
    DocumentParser create();
}
