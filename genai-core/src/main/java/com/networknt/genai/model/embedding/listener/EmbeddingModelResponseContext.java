package com.networknt.genai.model.embedding.listener;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.Experimental;
import com.networknt.genai.data.embedding.Embedding;
import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.model.output.Response;
import java.util.List;
import java.util.Map;

/**
 * The embedding model response context.
 * It contains the {@link Response}, corresponding input, the {@link EmbeddingModel} and attributes.
 * The attributes can be used to pass data between methods of an {@link EmbeddingModelListener}
 * or between multiple {@link EmbeddingModelListener}s.
 *
 * @since 1.11.0
 */
@Experimental
public class EmbeddingModelResponseContext {

    private final Response<List<Embedding>> response;
    private final List<TextSegment> textSegments;
    private final EmbeddingModel embeddingModel;
    private final Map<Object, Object> attributes;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public EmbeddingModelResponseContext(Builder builder) {
        this.response = ensureNotNull(builder.response, "response");
        this.textSegments = copy(ensureNotNull(builder.textSegments, "textSegments"));
        this.embeddingModel = ensureNotNull(builder.embeddingModel, "embeddingModel");
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
     * Builder for {@link EmbeddingModelResponseContext}.
     *
     * @since 1.11.0
     */
    @Experimental
    public static class Builder {

        private Response<List<Embedding>> response;
        private List<TextSegment> textSegments;
        private EmbeddingModel embeddingModel;
        private Map<Object, Object> attributes;

        Builder() {}

        /**
         * Sets the response.
         *
         * @param response the response
         * @return the builder
         */
        public Builder response(Response<List<Embedding>> response) {
            this.response = response;
            return this;
        }

        /**
         * Sets the text segments.
         *
         * @param textSegments the text segments
         * @return the builder
         */
        public Builder textSegments(List<TextSegment> textSegments) {
            this.textSegments = textSegments;
            return this;
        }

        /**
         * Sets the embedding model.
         *
         * @param embeddingModel the embedding model
         * @return the builder
         */
        public Builder embeddingModel(EmbeddingModel embeddingModel) {
            this.embeddingModel = embeddingModel;
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
        public EmbeddingModelResponseContext build() {
            return new EmbeddingModelResponseContext(this);
        }
    }

    /**
     * Returns the response.
     *
     * @return the response
     */
    public Response<List<Embedding>> response() {
        return response;
    }

    /**
     * Returns the text segments.
     *
     * @return the text segments
     */
    public List<TextSegment> textSegments() {
        return textSegments;
    }

    /**
     * Returns the embedding model.
     *
     * @return the embedding model
     */
    public EmbeddingModel embeddingModel() {
        return embeddingModel;
    }

    /**
     * Returns the attributes map.
     *
     * @return The attributes map. It can be used to pass data between methods of an {@link EmbeddingModelListener}
     * or between multiple {@link EmbeddingModelListener}s.
     */
    public Map<Object, Object> attributes() {
        return attributes;
    }
}
