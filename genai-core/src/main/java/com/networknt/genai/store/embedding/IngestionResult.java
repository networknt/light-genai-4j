package com.networknt.genai.store.embedding;

import com.networknt.genai.model.output.TokenUsage;

/**
 * Represents the result of a {@link EmbeddingStoreIngestor} ingestion process.
 */
public class IngestionResult {

    /**
     * The token usage information.
     */
    private final TokenUsage tokenUsage;


    /**
     * Creates a new ingestion result.
     *
     * @param tokenUsage the token usage
     */
    public IngestionResult(TokenUsage tokenUsage) {
        this.tokenUsage = tokenUsage;
    }

    /**
     * Returns the token usage information.
     *
     * @return the token usage
     */
    public TokenUsage tokenUsage() {
        return tokenUsage;
    }
}
