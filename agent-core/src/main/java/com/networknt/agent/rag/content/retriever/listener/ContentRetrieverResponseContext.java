package com.networknt.agent.rag.content.retriever.listener;

import static com.networknt.agent.internal.Utils.copy;
import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

import com.networknt.agent.Experimental;
import com.networknt.agent.rag.content.Content;
import com.networknt.agent.rag.content.retriever.ContentRetriever;
import com.networknt.agent.rag.query.Query;
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

    public ContentRetrieverResponseContext(Builder builder) {
        this.contents = copy(ensureNotNull(builder.contents, "contents"));
        this.query = ensureNotNull(builder.query, "query");
        this.contentRetriever = ensureNotNull(builder.contentRetriever, "contentRetriever");
        this.attributes = ensureNotNull(builder.attributes, "attributes");
    }

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

        public Builder contents(List<Content> contents) {
            this.contents = contents;
            return this;
        }

        public Builder query(Query query) {
            this.query = query;
            return this;
        }

        public Builder contentRetriever(ContentRetriever contentRetriever) {
            this.contentRetriever = contentRetriever;
            return this;
        }

        public Builder attributes(Map<Object, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public ContentRetrieverResponseContext build() {
            return new ContentRetrieverResponseContext(this);
        }
    }

    public List<Content> contents() {
        return contents;
    }

    public Query query() {
        return query;
    }

    public ContentRetriever contentRetriever() {
        return contentRetriever;
    }

    /**
     * @return The attributes map. It can be used to pass data between methods of a {@link ContentRetrieverListener}
     * or between multiple {@link ContentRetrieverListener}s.
     */
    public Map<Object, Object> attributes() {
        return attributes;
    }
}
