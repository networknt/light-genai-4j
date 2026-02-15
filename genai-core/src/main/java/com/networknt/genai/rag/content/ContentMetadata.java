package com.networknt.genai.rag.content;

/**
 * Metadata keys for {@link Content}.
 */
public enum ContentMetadata {
    /**
     * The score of the content.
     */
    SCORE,
    /**
     * The re-ranked score of the content.
     */
    RERANKED_SCORE,
    /**
     * The ID of the embedding associated with the content.
     */
    EMBEDDING_ID
}
