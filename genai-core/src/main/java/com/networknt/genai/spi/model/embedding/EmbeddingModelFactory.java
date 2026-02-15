package com.networknt.genai.spi.model.embedding;

import com.networknt.genai.Internal;
import com.networknt.genai.model.embedding.EmbeddingModel;

/**
 * A factory for creating {@link EmbeddingModel} instances through SPI.
 * <br>
 * For the "Easy RAG", import {@code langchain4j-easy-rag} module,
 * which contains a {@code EmbeddingModelFactory} implementation.
 */
@Internal
public interface EmbeddingModelFactory {

    /**
     * Creates a new {@link EmbeddingModel}.
     *
     * @return the embedding model
     */
    EmbeddingModel create();
}
