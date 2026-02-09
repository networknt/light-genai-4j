package com.networknt.agent.store.embedding;

import java.util.List;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

/**
 * Represents a result of a search in an {@link EmbeddingStore}.
 */
public class EmbeddingSearchResult<Embedded> {

    private final List<EmbeddingMatch<Embedded>> matches;

    public EmbeddingSearchResult(List<EmbeddingMatch<Embedded>> matches) {
        this.matches = ensureNotNull(matches, "matches");
    }

    public List<EmbeddingMatch<Embedded>> matches() {
        return matches;
    }
}
