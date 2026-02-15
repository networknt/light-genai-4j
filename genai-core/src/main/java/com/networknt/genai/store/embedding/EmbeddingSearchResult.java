package com.networknt.genai.store.embedding;

import java.util.List;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * Represents a result of a search in an {@link EmbeddingStore}.
 *
 * @param <Embedded> the type of the embedded content
 */
public class EmbeddingSearchResult<Embedded> {

    private final List<EmbeddingMatch<Embedded>> matches;

    /**
     * Creates a new instance.
     *
     * @param matches the matches
     */
    public EmbeddingSearchResult(List<EmbeddingMatch<Embedded>> matches) {
        this.matches = ensureNotNull(matches, "matches");
    }

    /**
     * Returns the matches.
     *
     * @return the matches
     */
    public List<EmbeddingMatch<Embedded>> matches() {
        return matches;
    }
}
