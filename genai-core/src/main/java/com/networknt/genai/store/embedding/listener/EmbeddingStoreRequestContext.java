package com.networknt.genai.store.embedding.listener;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.Experimental;
import com.networknt.genai.data.embedding.Embedding;
import com.networknt.genai.store.embedding.EmbeddingSearchRequest;
import com.networknt.genai.store.embedding.EmbeddingStore;
import com.networknt.genai.store.embedding.filter.Filter;
import java.util.List;
import java.util.Map;

/**
 * The embedding store request context.
 * It contains operation details and attributes.
 * The attributes can be used to pass data between methods of an {@link EmbeddingStoreListener}
 * or between multiple {@link EmbeddingStoreListener}s.
 *
 * @since 1.11.0
 * @param <Embedded> the type of the embedded content
 */
@Experimental
public abstract class EmbeddingStoreRequestContext<Embedded> {

    private final EmbeddingStore<Embedded> embeddingStore;
    private final Map<Object, Object> attributes;

    /**
     * Creates a new embedding store request context.
     *
     * @param embeddingStore the embedding store
     * @param attributes the attributes
     */
    protected EmbeddingStoreRequestContext(EmbeddingStore<Embedded> embeddingStore, Map<Object, Object> attributes) {
        this.embeddingStore = ensureNotNull(embeddingStore, "embeddingStore");
        this.attributes = ensureNotNull(attributes, "attributes");
    }

    /**
     * Returns the embedding store.
     *
     * @return The embedding store.
     */
    public EmbeddingStore<Embedded> embeddingStore() {
        return embeddingStore;
    }

    /**
     * Returns the attributes map.
     *
     * @return The attributes map. It can be used to pass data between methods of a {@link EmbeddingStoreListener}
     * or between multiple {@link EmbeddingStoreListener}s.
     */
    public Map<Object, Object> attributes() {
        return attributes;
    }

    /**
     * The {@code add(...)} request context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class Add<Embedded> extends EmbeddingStoreRequestContext<Embedded> {

        private final String id;
        private final Embedding embedding;
        private final Embedded embedded;

        /**
         * Creates a new add request context.
         *
         * @param embeddingStore the embedding store
         * @param attributes the attributes
         * @param embedding the embedding
         */
        public Add(EmbeddingStore<Embedded> embeddingStore, Map<Object, Object> attributes, Embedding embedding) {
            this(embeddingStore, attributes, null, embedding, null);
        }

        /**
         * Creates a new add request context.
         *
         * @param embeddingStore the embedding store
         * @param attributes the attributes
         * @param id the id
         * @param embedding the embedding
         */
        public Add(
                EmbeddingStore<Embedded> embeddingStore,
                Map<Object, Object> attributes,
                String id,
                Embedding embedding) {
            this(embeddingStore, attributes, id, embedding, null);
        }

        /**
         * Creates a new add request context.
         *
         * @param embeddingStore the embedding store
         * @param attributes the attributes
         * @param id the id
         * @param embedding the embedding
         * @param embedded the embedded
         */
        public Add(
                EmbeddingStore<Embedded> embeddingStore,
                Map<Object, Object> attributes,
                String id,
                Embedding embedding,
                Embedded embedded) {
            super(embeddingStore, attributes);
            this.id = id;
            this.embedding = embedding;
            this.embedded = embedded;
        }

        /**
         * Returns the ID argument.
         *
         * @return The ID argument for operations like {@code add(String, Embedding)} (if applicable).
         */
        public String id() {
            return id;
        }

        /**
         * Returns the embedding argument.
         *
         * @return The embedding argument for {@code add(...)} operations.
         */
        public Embedding embedding() {
            return embedding;
        }

        /**
         * Returns the original embedded content.
         *
         * @return The original embedded content for {@code add(Embedding, Embedded)} (if applicable).
         */
        public Embedded embedded() {
            return embedded;
        }
    }

    /**
     * The {@code addAll(...)} request context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class AddAll<Embedded> extends EmbeddingStoreRequestContext<Embedded> {

        private final List<String> ids;
        private final List<Embedding> embeddings;
        private final List<Embedded> embeddedList;

        /**
         * Creates a new add all request context.
         *
         * @param embeddingStore the embedding store
         * @param attributes the attributes
         * @param ids the ids
         * @param embeddings the embeddings
         * @param embeddedList the embedded list
         */
        public AddAll(
                EmbeddingStore<Embedded> embeddingStore,
                Map<Object, Object> attributes,
                List<String> ids,
                List<Embedding> embeddings,
                List<Embedded> embeddedList) {
            super(embeddingStore, attributes);
            this.ids = copy(ids);
            this.embeddings = copy(embeddings);
            this.embeddedList = copy(embeddedList);
        }

        /**
         * Returns the IDs argument.
         *
         * @return The IDs argument for operations like {@code addAll(ids, ...)} (if applicable).
         */
        public List<String> ids() {
            return ids;
        }

        /**
         * Returns the embeddings argument.
         *
         * @return The embeddings argument for {@code addAll(...)} operations.
         */
        public List<Embedding> embeddings() {
            return embeddings;
        }

        /**
         * Returns the list of embedded contents.
         *
         * @return The list of embedded contents for {@code addAll(..., embedded)} (if applicable).
         */
        public List<Embedded> embeddedList() {
            return embeddedList;
        }
    }

    /**
     * The {@code search(...)} request context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class Search<Embedded> extends EmbeddingStoreRequestContext<Embedded> {

        private final EmbeddingSearchRequest searchRequest;

        /**
         * Creates a new search request context.
         *
         * @param embeddingStore the embedding store
         * @param attributes the attributes
         * @param searchRequest the search request
         */
        public Search(
                EmbeddingStore<Embedded> embeddingStore,
                Map<Object, Object> attributes,
                EmbeddingSearchRequest searchRequest) {
            super(embeddingStore, attributes);
            this.searchRequest = searchRequest;
        }

        /**
         * Returns the search request.
         *
         * @return The search request for {@code search(...)}.
         */
        public EmbeddingSearchRequest searchRequest() {
            return searchRequest;
        }
    }

    /**
     * The {@code remove(String)} request context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class Remove<Embedded> extends EmbeddingStoreRequestContext<Embedded> {

        private final String id;

        /**
         * Creates a new remove request context.
         *
         * @param embeddingStore the embedding store
         * @param attributes the attributes
         * @param id the id
         */
        public Remove(EmbeddingStore<Embedded> embeddingStore, Map<Object, Object> attributes, String id) {
            super(embeddingStore, attributes);
            this.id = id;
        }

        /**
         * Returns the ID argument.
         *
         * @return The ID argument for operations like {@code remove(String)}.
         */
        public String id() {
            return id;
        }
    }

    /**
     * The {@code removeAll(ids)} request context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class RemoveAllIds<Embedded> extends EmbeddingStoreRequestContext<Embedded> {

        private final List<String> ids;

        /**
         * Creates a new remove all ids request context.
         *
         * @param embeddingStore the embedding store
         * @param attributes the attributes
         * @param ids the ids
         */
        public RemoveAllIds(EmbeddingStore<Embedded> embeddingStore, Map<Object, Object> attributes, List<String> ids) {
            super(embeddingStore, attributes);
            this.ids = copy(ids);
        }

        /**
         * Returns the IDs argument.
         *
         * @return The IDs argument for operations like {@code removeAll(ids)}.
         */
        public List<String> ids() {
            return ids;
        }
    }

    /**
     * The {@code removeAll(Filter)} request context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class RemoveAllFilter<Embedded> extends EmbeddingStoreRequestContext<Embedded> {

        private final Filter filter;

        /**
         * Creates a new remove all filter request context.
         *
         * @param embeddingStore the embedding store
         * @param attributes the attributes
         * @param filter the filter
         */
        public RemoveAllFilter(EmbeddingStore<Embedded> embeddingStore, Map<Object, Object> attributes, Filter filter) {
            super(embeddingStore, attributes);
            this.filter = filter;
        }

        /**
         * Returns the filter argument.
         *
         * @return The filter argument for {@code removeAll(Filter)}.
         */
        public Filter filter() {
            return filter;
        }
    }

    /**
     * The {@code removeAll()} request context.
     *
     * @since 1.11.0
     * @param <Embedded> the type of the embedded content
     */
    @Experimental
    public static final class RemoveAll<Embedded> extends EmbeddingStoreRequestContext<Embedded> {

        /**
         * Creates a new remove all request context.
         *
         * @param embeddingStore the embedding store
         * @param attributes the attributes
         */
        public RemoveAll(EmbeddingStore<Embedded> embeddingStore, Map<Object, Object> attributes) {
            super(embeddingStore, attributes);
        }
    }
}
