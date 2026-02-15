package com.networknt.genai.rag.content.retriever;

import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureBetween;
import static com.networknt.genai.internal.ValidationUtils.ensureGreaterThanZero;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static com.networknt.genai.spi.ServiceHelper.loadFactories;

import com.networknt.genai.data.embedding.Embedding;
import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.rag.content.Content;
import com.networknt.genai.rag.content.ContentMetadata;
import com.networknt.genai.rag.query.Query;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;
import com.networknt.genai.store.embedding.EmbeddingSearchRequest;
import com.networknt.genai.store.embedding.EmbeddingSearchResult;
import com.networknt.genai.store.embedding.EmbeddingStore;
import com.networknt.genai.store.embedding.filter.Filter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A {@link ContentRetriever} that retrieves from an {@link EmbeddingStore}.
 * <br>
 * By default, it retrieves the 3 most similar {@link Content}s to the provided {@link Query},
 * without any {@link Filter}ing.
 * <br>
 * <br>
 * Configurable parameters (optional):
 * <br>
 * - {@code displayName}: Display name for logging purposes, e.g. when multiple instances are used.
 * <br>
 * - {@code maxResults}: The maximum number of {@link Content}s to retrieve.
 * <br>
 * - {@code dynamicMaxResults}: It is a {@link Function} that accepts a {@link Query} and returns a {@code maxResults} value.
 * It can be used to dynamically define {@code maxResults} value, depending on factors such as the query,
 * the user (using Metadata#chatMemoryId()} from {@link Query#metadata()}), etc.
 * <br>
 * - {@code minScore}: The minimum relevance score for the returned {@link Content}s.
 * {@link Content}s scoring below {@code #minScore} are excluded from the results.
 * <br>
 * - {@code dynamicMinScore}: It is a {@link Function} that accepts a {@link Query} and returns a {@code minScore} value.
 * It can be used to dynamically define {@code minScore} value, depending on factors such as the query,
 * the user (using Metadata#chatMemoryId()} from {@link Query#metadata()}), etc.
 * <br>
 * - {@code filter}: The {@link Filter} that will be applied to a {@link com.networknt.genai.data.document.Metadata} in the
 * {@link Content#textSegment()}.
 * <br>
 * - {@code dynamicFilter}: It is a {@link Function} that accepts a {@link Query} and returns a {@code filter} value.
 * It can be used to dynamically define {@code filter} value, depending on factors such as the query,
 * the user (using Metadata#chatMemoryId()} from {@link Query#metadata()}), etc.
 */
public class EmbeddingStoreContentRetriever implements ContentRetriever {

    /**
     * The default max results provider.
     */
    public static final Function<Query, Integer> DEFAULT_MAX_RESULTS = (query) -> 3;

    /**
     * The default min score provider.
     */
    public static final Function<Query, Double> DEFAULT_MIN_SCORE = (query) -> 0.0;

    /**
     * The default filter provider.
     */
    public static final Function<Query, Filter> DEFAULT_FILTER = (query) -> null;

    /**
     * The default display name.
     */
    public static final String DEFAULT_DISPLAY_NAME = "Default";

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    private final Function<Query, Integer> maxResultsProvider;
    private final Function<Query, Double> minScoreProvider;
    private final Function<Query, Filter> filterProvider;

    private final String displayName;

    /**
     * Creates a new embedding store content retriever.
     *
     * @param embeddingStore the embedding store
     * @param embeddingModel the embedding model
     */
    public EmbeddingStoreContentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        this(
                DEFAULT_DISPLAY_NAME,
                embeddingStore,
                embeddingModel,
                DEFAULT_MAX_RESULTS,
                DEFAULT_MIN_SCORE,
                DEFAULT_FILTER);
    }

    /**
     * Creates a new embedding store content retriever.
     *
     * @param embeddingStore the embedding store
     * @param embeddingModel the embedding model
     * @param maxResults     the maximum number of results
     */
    public EmbeddingStoreContentRetriever(
            EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel, int maxResults) {
        this(
                DEFAULT_DISPLAY_NAME,
                embeddingStore,
                embeddingModel,
                (query) -> maxResults,
                DEFAULT_MIN_SCORE,
                DEFAULT_FILTER);
    }

    /**
     * Creates a new embedding store content retriever.
     *
     * @param embeddingStore the embedding store
     * @param embeddingModel the embedding model
     * @param maxResults     the maximum number of results
     * @param minScore       the minimum score
     */
    public EmbeddingStoreContentRetriever(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel,
            Integer maxResults,
            Double minScore) {
        this(
                DEFAULT_DISPLAY_NAME,
                embeddingStore,
                embeddingModel,
                (query) -> maxResults,
                (query) -> minScore,
                DEFAULT_FILTER);
    }

    private EmbeddingStoreContentRetriever(
            String displayName,
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel,
            Function<Query, Integer> dynamicMaxResults,
            Function<Query, Double> dynamicMinScore,
            Function<Query, Filter> dynamicFilter) {
        this.displayName = getOrDefault(displayName, DEFAULT_DISPLAY_NAME);
        this.embeddingStore = ensureNotNull(embeddingStore, "embeddingStore");
        this.embeddingModel = ensureNotNull(
                getOrDefault(embeddingModel, EmbeddingStoreContentRetriever::loadEmbeddingModel), "embeddingModel");
        this.maxResultsProvider = getOrDefault(dynamicMaxResults, DEFAULT_MAX_RESULTS);
        this.minScoreProvider = getOrDefault(dynamicMinScore, DEFAULT_MIN_SCORE);
        this.filterProvider = getOrDefault(dynamicFilter, DEFAULT_FILTER);
    }

    private static EmbeddingModel loadEmbeddingModel() {
        Collection<EmbeddingModelFactory> factories = loadFactories(EmbeddingModelFactory.class);
        if (factories.size() > 1) {
            throw new RuntimeException("Conflict: multiple embedding models have been found in the classpath. "
                    + "Please explicitly specify the one you wish to use.");
        }

        for (EmbeddingModelFactory factory : factories) {
            return factory.create();
        }

        return null;
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    public static EmbeddingStoreContentRetrieverBuilder builder() {
        return new EmbeddingStoreContentRetrieverBuilder();
    }

    /**
     * Builder for {@link EmbeddingStoreContentRetriever}.
     */
    public static class EmbeddingStoreContentRetrieverBuilder {

        private String displayName;
        private EmbeddingStore<TextSegment> embeddingStore;
        private EmbeddingModel embeddingModel;
        private Function<Query, Integer> dynamicMaxResults;
        private Function<Query, Double> dynamicMinScore;
        private Function<Query, Filter> dynamicFilter;

        EmbeddingStoreContentRetrieverBuilder() {}

        /**
         * Sets the maximum results.
         *
         * @param maxResults the maximum results
         * @return the builder
         */
        public EmbeddingStoreContentRetrieverBuilder maxResults(Integer maxResults) {
            if (maxResults != null) {
                dynamicMaxResults = (query) -> ensureGreaterThanZero(maxResults, "maxResults");
            }
            return this;
        }

        /**
         * Sets the minimum score.
         *
         * @param minScore the minimum score
         * @return the builder
         */
        public EmbeddingStoreContentRetrieverBuilder minScore(Double minScore) {
            if (minScore != null) {
                dynamicMinScore = (query) -> ensureBetween(minScore, 0, 1, "minScore");
            }
            return this;
        }

        /**
         * Sets the filter.
         *
         * @param filter the filter
         * @return the builder
         */
        public EmbeddingStoreContentRetrieverBuilder filter(Filter filter) {
            if (filter != null) {
                dynamicFilter = (query) -> filter;
            }
            return this;
        }

        /**
         * Sets the display name.
         *
         * @param displayName the display name
         * @return the builder
         */
        public EmbeddingStoreContentRetrieverBuilder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        /**
         * Sets the embedding store.
         *
         * @param embeddingStore the embedding store
         * @return the builder
         */
        public EmbeddingStoreContentRetrieverBuilder embeddingStore(EmbeddingStore<TextSegment> embeddingStore) {
            this.embeddingStore = embeddingStore;
            return this;
        }

        /**
         * Sets the embedding model.
         *
         * @param embeddingModel the embedding model
         * @return the builder
         */
        public EmbeddingStoreContentRetrieverBuilder embeddingModel(EmbeddingModel embeddingModel) {
            this.embeddingModel = embeddingModel;
            return this;
        }

        /**
         * Sets the dynamic max results provider.
         *
         * @param dynamicMaxResults the dynamic max results provider
         * @return the builder
         */
        public EmbeddingStoreContentRetrieverBuilder dynamicMaxResults(Function<Query, Integer> dynamicMaxResults) {
            this.dynamicMaxResults = dynamicMaxResults;
            return this;
        }

        /**
         * Sets the dynamic min score provider.
         *
         * @param dynamicMinScore the dynamic min score provider
         * @return the builder
         */
        public EmbeddingStoreContentRetrieverBuilder dynamicMinScore(Function<Query, Double> dynamicMinScore) {
            this.dynamicMinScore = dynamicMinScore;
            return this;
        }

        /**
         * Sets the dynamic filter provider.
         *
         * @param dynamicFilter the dynamic filter provider
         * @return the builder
         */
        public EmbeddingStoreContentRetrieverBuilder dynamicFilter(Function<Query, Filter> dynamicFilter) {
            this.dynamicFilter = dynamicFilter;
            return this;
        }

        /**
         * Builds the embedding store content retriever.
         *
         * @return the embedding store content retriever
         */
        public EmbeddingStoreContentRetriever build() {
            return new EmbeddingStoreContentRetriever(
                    this.displayName,
                    this.embeddingStore,
                    this.embeddingModel,
                    this.dynamicMaxResults,
                    this.dynamicMinScore,
                    this.dynamicFilter);
        }
    }

    /**
     * Creates an instance of an {@code EmbeddingStoreContentRetriever} from the specified {@link EmbeddingStore}
     * and {@link EmbeddingModel} found through SPI (see {@link EmbeddingModelFactory}).
     *
     * @param embeddingStore the embedding store
     * @return the content retriever
     */
    public static EmbeddingStoreContentRetriever from(EmbeddingStore<TextSegment> embeddingStore) {
        return builder().embeddingStore(embeddingStore).build();
    }

    @Override
    public List<Content> retrieve(Query query) {

        Embedding embeddedQuery = embeddingModel.embed(query.text()).content();

        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .query(query.text())
                .queryEmbedding(embeddedQuery)
                .maxResults(maxResultsProvider.apply(query))
                .minScore(minScoreProvider.apply(query))
                .filter(filterProvider.apply(query))
                .build();

        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        return searchResult.matches().stream()
                .map(embeddingMatch -> Content.from(
                        embeddingMatch.embedded(),
                        Map.of(
                                ContentMetadata.SCORE, embeddingMatch.score(),
                                ContentMetadata.EMBEDDING_ID, embeddingMatch.embeddingId())))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "EmbeddingStoreContentRetriever{" + "displayName='" + displayName + '\'' + '}';
    }
}
