package com.networknt.genai.store.embedding.listener;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.Experimental;
import com.networknt.genai.store.embedding.EmbeddingSearchResult;
import com.networknt.genai.store.embedding.EmbeddingStore;
import java.util.List;
import java.util.Map;

/**
 * The embedding store response context.
 * It contains the response details, corresponding request details, and attributes.
 * The attributes can be used to pass data between methods of an {@link EmbeddingStoreListener}
 * or between multiple {@link EmbeddingStoreListener}s.
 *
 * @since 1.11.0
 * @param <Embedded> the type of the embedded content
 */
@Experimental
public abstract class EmbeddingStoreResponseContext<Embedded> {

    private final EmbeddingStoreRequestContext<Embedded> requestContext;
    private final Map<Object, Object> attributes;

    /**
     * Creates a new embedding store response context.
     *
     * @param requestContext the request context
     * @param attributes the attributes
     */
    protected EmbeddingStoreResponseContext(
            EmbeddingStoreRequestContext<Embedded> requestContext, Map<Object, Object> attributes) {
        this.requestContext = ensureNotNull(requestContext, "requestContext");
        this.attributes = ensureNotNull(attributes, "attributes");
    }

    /**
     * Returns the embedding store.
     *
     * @return The embedding store.
     */
    public EmbeddingStore<Embedded> embeddingStore() {
        return requestContext.embeddingStore();
    }

    /**
     * Returns the attributes map.
     *
     * @return The attributes map. It can be used to pass data between methods of an {@link EmbeddingStoreListener}
     * or between multiple {@link EmbeddingStoreListener}s.
     */
    public Map<Object, Object> attributes() {
        return attributes;
    }

    /**
     * Returns the corresponding request context.
     *
     * @return The corresponding request context.
     */
    public EmbeddingStoreRequestContext<Embedded> requestContext() {
        return requestContext;
    }

    /**
     * The {@code add(...)} response context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class Add<Embedded> extends EmbeddingStoreResponseContext<Embedded> {

        private final String returnedId;

        /**
         * Creates a new add response context.
         *
         * @param requestContext the request context
         * @param attributes the attributes
         * @param returnedId the returned id
         */
        public Add(
                EmbeddingStoreRequestContext<Embedded> requestContext,
                Map<Object, Object> attributes,
                String returnedId) {
            super(requestContext, attributes);
            this.returnedId = returnedId;
        }

        /**
         * Returns the returned ID.
         *
         * @return The returned ID for operations like {@code add(Embedding)} and {@code add(Embedding, Embedded)} (if applicable).
         */
        public String returnedId() {
            return returnedId;
        }
    }

    /**
     * The {@code addAll(...)} response context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class AddAll<Embedded> extends EmbeddingStoreResponseContext<Embedded> {

        private final List<String> returnedIds;

        /**
         * Creates a new add all response context.
         *
         * @param requestContext the request context
         * @param attributes the attributes
         * @param returnedIds the returned ids
         */
        public AddAll(
                EmbeddingStoreRequestContext<Embedded> requestContext,
                Map<Object, Object> attributes,
                List<String> returnedIds) {
            super(requestContext, attributes);
            this.returnedIds = copy(returnedIds);
        }

        /**
         * Returns the returned IDs.
         *
         * @return The returned IDs for operations like {@code addAll(List<Embedding>)} and {@code addAll(List<Embedding>, List<Embedded>)} (if applicable).
         */
        public List<String> returnedIds() {
            return returnedIds;
        }
    }

    /**
     * The {@code search(...)} response context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class Search<Embedded> extends EmbeddingStoreResponseContext<Embedded> {

        private final EmbeddingSearchResult<Embedded> searchResult;

        /**
         * Creates a new search response context.
         *
         * @param requestContext the request context
         * @param attributes the attributes
         * @param searchResult the search result
         */
        public Search(
                EmbeddingStoreRequestContext<Embedded> requestContext,
                Map<Object, Object> attributes,
                EmbeddingSearchResult<Embedded> searchResult) {
            super(requestContext, attributes);
            this.searchResult = searchResult;
        }

        /**
         * Returns the search result.
         *
         * @return The search result for {@code search(...)}.
         */
        public EmbeddingSearchResult<Embedded> searchResult() {
            return searchResult;
        }
    }

    /**
     * The {@code remove(String)} response context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class Remove<Embedded> extends EmbeddingStoreResponseContext<Embedded> {

        /**
         * Creates a new remove response context.
         *
         * @param requestContext the request context
         * @param attributes the attributes
         */
        public Remove(EmbeddingStoreRequestContext.Remove<Embedded> requestContext, Map<Object, Object> attributes) {
            super(requestContext, attributes);
        }
    }

    /**
     * The {@code removeAll(ids)} response context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class RemoveAllIds<Embedded> extends EmbeddingStoreResponseContext<Embedded> {

        /**
         * Creates a new remove all ids response context.
         *
         * @param requestContext the request context
         * @param attributes the attributes
         */
        public RemoveAllIds(
                EmbeddingStoreRequestContext.RemoveAllIds<Embedded> requestContext, Map<Object, Object> attributes) {
            super(requestContext, attributes);
        }
    }

    /**
     * The {@code removeAll(Filter)} response context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class RemoveAllFilter<Embedded> extends EmbeddingStoreResponseContext<Embedded> {

        /**
         * Creates a new remove all filter response context.
         *
         * @param requestContext the request context
         * @param attributes the attributes
         */
        public RemoveAllFilter(
                EmbeddingStoreRequestContext.RemoveAllFilter<Embedded> requestContext, Map<Object, Object> attributes) {
            super(requestContext, attributes);
        }
    }

    /**
     * The {@code removeAll()} response context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class RemoveAll<Embedded> extends EmbeddingStoreResponseContext<Embedded> {

        /**
         * Creates a new remove all response context.
         *
         * @param requestContext the request context
         * @param attributes the attributes
         */
        public RemoveAll(
                EmbeddingStoreRequestContext.RemoveAll<Embedded> requestContext, Map<Object, Object> attributes) {
            super(requestContext, attributes);
        }
    }
}
