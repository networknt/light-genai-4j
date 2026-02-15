package com.networknt.genai.rag.content.retriever.listener;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.Experimental;
import com.networknt.genai.rag.content.Content;
import com.networknt.genai.rag.content.retriever.ContentRetriever;
import com.networknt.genai.rag.query.Query;
import java.util.List;
import java.util.Map;

/**
 * The content retriever response context.
 * It contains retrieved {@link Content}s, corresponding {@link Query}, {@link ContentRetriever} and attributes.
 * The attributes can be used to pass data between methods of a {@link ContentRetrieverListener}
 * or between multiple {@link ContentRetrieverListener}s.
 *
 * @since 1.11.0
 */
@Experimental
public class ContentRetrieverResponseContext {

    private final List<Content> contents;
    private final Query query;
    private final ContentRetriever contentRetriever;
    private final Map<Object, Object> attributes;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public ContentRetrieverResponseContext(Builder builder) {
        this.contents = copy(ensureNotNull(builder.contents, "contents"));
        this.query = ensureNotNull(builder.query, "query");
        this.contentRetriever = ensureNotNull(builder.contentRetriever, "contentRetriever");
        this.attributes = ensureNotNull(builder.attributes, "attributes");
    }

    /**
     * Creates a new builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link ContentRetrieverResponseContext}.
     *
     * @since 1.11.0
     */
    @Experimental
    public static class Builder {

        private List<Content> contents;
        private Query query;
        private ContentRetriever contentRetriever;
        private Map<Object, Object> attributes;

        Builder() {}

        /**
         * Sets the contents.
         *
         * @param contents the contents
         * @return the builder
         */
        public Builder contents(List<Content> contents) {
            this.contents = contents;
            return this;
        }

        /**
         * Sets the query.
         *
         * @param query the query
         * @return the builder
         */
        public Builder query(Query query) {
            this.query = query;
            return this;
        }

        /**
         * Sets the content retriever.
         *
         * @param contentRetriever the content retriever
         * @return the builder
         */
        public Builder contentRetriever(ContentRetriever contentRetriever) {
            this.contentRetriever = contentRetriever;
            return this;
        }

        /**
         * Sets the attributes.
         *
         * @param attributes the attributes
         * @return the builder
         */
        public Builder attributes(Map<Object, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        /**
         * Builds the context.
         *
         * @return the context
         */
        public ContentRetrieverResponseContext build() {
            return new ContentRetrieverResponseContext(this);
        }
    }

    /**
     * Returns the contents.
     *
     * @return the contents
     */
    public List<Content> contents() {
        return contents;
    }

    /**
     * Returns the query.
     *
     * @return the query
     */
    public Query query() {
        return query;
    }

    /**
     * Returns the content retriever.
     *
     * @return the content retriever
     */
    public ContentRetriever contentRetriever() {
        return contentRetriever;
    }

    /**
     * Returns the attributes.
     *
     * @return The attributes map. It can be used to pass data between methods of a {@link ContentRetrieverListener}
     * or between multiple {@link ContentRetrieverListener}s.
     */
    public Map<Object, Object> attributes() {
        return attributes;
    }
}
