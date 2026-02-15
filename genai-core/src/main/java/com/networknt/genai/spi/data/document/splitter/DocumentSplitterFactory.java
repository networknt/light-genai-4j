package com.networknt.genai.spi.data.document.splitter;

import com.networknt.genai.Internal;
import com.networknt.genai.data.document.DocumentSplitter;

/**
 * A factory for creating {@link DocumentSplitter} instances through SPI.
 * <br>
 * Available implementations: {@code RecursiveDocumentSplitterFactory}
 * in the {@code langchain4j-easy-rag} module.
 */
@Internal
public interface DocumentSplitterFactory {

    /**
     * Creates a new {@link DocumentSplitter}.
     *
     * @return the document splitter
     */
    DocumentSplitter create();
}
