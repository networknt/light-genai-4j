package com.networknt.genai.store.embedding.listener;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.Experimental;
import com.networknt.genai.store.embedding.EmbeddingStore;
import java.util.Map;

/**
 * The embedding store error context.
 * It contains the error, corresponding request details, and attributes.
 * The attributes can be used to pass data between methods of an {@link EmbeddingStoreListener}
 * or between multiple {@link EmbeddingStoreListener}s.
 *
 * @since 1.11.0
 * @param <Embedded> the type of the embedded content
 */
@Experimental
public class EmbeddingStoreErrorContext<Embedded> {

    private final Throwable error;
    private final EmbeddingStoreRequestContext<Embedded> requestContext;
    private final Map<Object, Object> attributes;

    /**
     * Creates a new error context.
     *
     * @param error the error
     * @param requestContext the request context
     * @param attributes the attributes
     */
    public EmbeddingStoreErrorContext(
            Throwable error, EmbeddingStoreRequestContext<Embedded> requestContext, Map<Object, Object> attributes) {
        this.error = ensureNotNull(error, "error");
        this.requestContext = ensureNotNull(requestContext, "requestContext");
        this.attributes = ensureNotNull(attributes, "attributes");
    }

    /**
     * Returns the error that occurred.
     *
     * @return The error that occurred.
     */
    public Throwable error() {
        return error;
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
     * Returns the corresponding request context.
     *
     * @return The corresponding request context.
     */
    public EmbeddingStoreRequestContext<Embedded> requestContext() {
        return requestContext;
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
}
