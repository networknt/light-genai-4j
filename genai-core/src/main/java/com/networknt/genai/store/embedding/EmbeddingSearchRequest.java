package com.networknt.genai.store.embedding;

import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureBetween;
import static com.networknt.genai.internal.ValidationUtils.ensureGreaterThanZero;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.data.document.Metadata;
import com.networknt.genai.data.embedding.Embedding;
import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.store.embedding.filter.Filter;
import java.util.Objects;

/**
 * Represents a request to search in an {@link EmbeddingStore}.
 */
public class EmbeddingSearchRequest {

    private final String query;
    private final Embedding queryEmbedding;
    private final int maxResults;
    private final double minScore;
    private final Filter filter;

    /**
     * Creates an instance of an EmbeddingSearchRequest.
     *
     * @param queryEmbedding The embedding used as a reference. Found embeddings should be similar to this one.
     *                       This is a mandatory parameter.
     * @param maxResults     The maximum number of embeddings to return. This is an optional parameter. Default: 3
     * @param minScore       The minimum score, ranging from 0 to 1 (inclusive).
     *                       Only embeddings with a score &gt;= minScore will be returned.
     *                       This is an optional parameter. Default: 0
     * @param filter         The filter to be applied to the {@link Metadata} during search.
     *                       Only {@link TextSegment}s whose {@link Metadata}
     *                       matches the {@link Filter} will be returned.
     *                       Please note that not all {@link EmbeddingStore}s support this feature yet.
     *                       This is an optional parameter. Default: no filtering
     */
    public EmbeddingSearchRequest(Embedding queryEmbedding, Integer maxResults, Double minScore, Filter filter) {
        this(builder().queryEmbedding(queryEmbedding).maxResults(maxResults).minScore(minScore).filter(filter));
    }

    /**
     * Creates an instance of an EmbeddingSearchRequest.
     *
     * @param builder The builder used to create the instance.
     */
    public EmbeddingSearchRequest(EmbeddingSearchRequestBuilder builder) {
        this.query = builder.query;
        this.queryEmbedding = ensureNotNull(builder.queryEmbedding, "queryEmbedding");
        this.maxResults = ensureGreaterThanZero(getOrDefault(builder.maxResults, 3), "maxResults");
        this.minScore = ensureBetween(getOrDefault(builder.minScore, 0.0), 0.0, 1.0, "minScore");
        this.filter = builder.filter;
    }

    /**
     * Returns the query.
     *
     * @return the query
     */
    public String query() {
        return query;
    }

    /**
     * Returns the query embedding.
     *
     * @return the query embedding
     */
    public Embedding queryEmbedding() {
        return queryEmbedding;
    }

    /**
     * Returns the maximum number of results.
     *
     * @return the maximum number of results
     */
    public int maxResults() {
        return maxResults;
    }

    /**
     * Returns the minimum score.
     *
     * @return the minimum score
     */
    public double minScore() {
        return minScore;
    }

    /**
     * Returns the filter.
     *
     * @return the filter
     */
    public Filter filter() {
        return filter;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EmbeddingSearchRequest that = (EmbeddingSearchRequest) o;
        return maxResults == that.maxResults
                && Double.compare(minScore, that.minScore) == 0
                && Objects.equals(query, that.query)
                && Objects.equals(queryEmbedding, that.queryEmbedding)
                && Objects.equals(filter, that.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, queryEmbedding, maxResults, minScore, filter);
    }

    @Override
    public String toString() {
        return "EmbeddingSearchRequest{" +
                "query='" + query + '\'' +
                ", queryEmbedding=" + queryEmbedding +
                ", maxResults=" + maxResults +
                ", minScore=" + minScore +
                ", filter=" + filter +
                '}';
    }

    /**
     * Creates a new builder.
     *
     * @return the builder
     */
    public static EmbeddingSearchRequestBuilder builder() {
        return new EmbeddingSearchRequestBuilder();
    }

    /**
     * Builder for {@link EmbeddingSearchRequest}.
     */
    public static class EmbeddingSearchRequestBuilder {

        private String query;
        private Embedding queryEmbedding;
        private Integer maxResults;
        private Double minScore;
        private Filter filter;

        EmbeddingSearchRequestBuilder() {}

        /**
         * The query used for search.
         * This is an optional parameter that can be used by {@link EmbeddingStore} implementations to support hybrid search.
         *
         * @param query the query
         * @return the builder
         */
        public EmbeddingSearchRequestBuilder query(String query) {
            this.query = query;
            return this;
        }

        /**
         * The embedding used as a reference. Found embeddings should be similar to this one.
         * This is a mandatory parameter.
         *
         * @param queryEmbedding the query embedding
         * @return the builder
         */
        public EmbeddingSearchRequestBuilder queryEmbedding(Embedding queryEmbedding) {
            this.queryEmbedding = queryEmbedding;
            return this;
        }

        /**
         * The maximum number of embeddings to return.
         * This is an optional parameter.
         * Default: 3
         *
         * @param maxResults the maximum number of results
         * @return the builder
         */
        public EmbeddingSearchRequestBuilder maxResults(Integer maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        /**
         * The minimum score, ranging from 0 to 1 (inclusive).
         * Only embeddings with a score &gt;= minScore will be returned.
         * This is an optional parameter.
         * Default: 0
         *
         * @param minScore the minimum score
         * @return the builder
         */
        public EmbeddingSearchRequestBuilder minScore(Double minScore) {
            this.minScore = minScore;
            return this;
        }

        /**
         * The filter to be applied to the {@link Metadata} during search.
         * Only {@link TextSegment}s whose {@link Metadata} matches the {@link Filter} will be returned.
         * Please note that not all {@link EmbeddingStore}s support this feature yet.
         * This is an optional parameter.
         * Default: no filtering
         *
         * @param filter the filter
         * @return the builder
         */
        public EmbeddingSearchRequestBuilder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Builds the request.
         *
         * @return the request
         */
        public EmbeddingSearchRequest build() {
            return new EmbeddingSearchRequest(this);
        }
    }
}
