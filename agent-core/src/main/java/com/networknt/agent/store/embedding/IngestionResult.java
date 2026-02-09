package com.networknt.agent.store.embedding;

import com.networknt.agent.model.output.TokenUsage;

/**
 * Represents the result of a {@link EmbeddingStoreIngestor} ingestion process.
 */
public class IngestionResult {

    /**
     * The token usage information.
     */
    private final TokenUsage tokenUsage;


    public IngestionResult(TokenUsage tokenUsage) {
        this.tokenUsage = tokenUsage;
    }

    public TokenUsage tokenUsage() {
        return tokenUsage;
    }
}
