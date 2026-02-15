package com.networknt.genai.model.chat.response;

import com.networknt.genai.internal.JacocoIgnoreCoverageGenerated;
import com.networknt.genai.model.output.FinishReason;
import com.networknt.genai.model.output.TokenUsage;

import java.util.Objects;

/**
 * Represents common chat response metadata supported by most LLM providers.
 * Specific LLM provider integrations can extend this interface to add provider-specific metadata.
 */
@JacocoIgnoreCoverageGenerated
public class ChatResponseMetadata {

    private final String id;
    private final String modelName;
    private final TokenUsage tokenUsage;
    private final FinishReason finishReason;

    /**
     * Creates a new chat response metadata.
     *
     * @param builder the builder
     */
    protected ChatResponseMetadata(Builder<?> builder) {
        this.id = builder.id;
        this.modelName = builder.modelName;
        this.tokenUsage = builder.tokenUsage;
        this.finishReason = builder.finishReason;
    }

    /**
     * Returns the id of the response.
     *
     * @return the id of the response
     */
    public String id() {
        return id;
    }

    /**
     * Returns the name of the model.
     *
     * @return the name of the model
     */
    public String modelName() {
        return modelName;
    }

    /**
     * Returns the token usage.
     *
     * @return the token usage
     */
    public TokenUsage tokenUsage() {
        return tokenUsage;
    }

    /**
     * Returns the finish reason.
     *
     * @return the finish reason
     */
    public FinishReason finishReason() {
        return finishReason;
    }

    /**
     * Returns a new builder with the current values.
     *
     * @return a new builder with the current values
     */
    public Builder<?> toBuilder() {
        return toBuilder(builder());
    }

    /**
     * Returns a new builder with the current values.
     *
     * @param builder the builder to populate
     * @return the populated builder
     */
    protected Builder<?> toBuilder(Builder<?> builder) {
        return builder
                .id(id)
                .modelName(modelName)
                .tokenUsage(tokenUsage)
                .finishReason(finishReason);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatResponseMetadata that = (ChatResponseMetadata) o;
        return Objects.equals(id, that.id)
                && Objects.equals(modelName, that.modelName)
                && Objects.equals(tokenUsage, that.tokenUsage)
                && Objects.equals(finishReason, that.finishReason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modelName, tokenUsage, finishReason);
    }

    @Override
    public String toString() {
        return "ChatResponseMetadata{" +
                "id='" + id + '\'' +
                ", modelName='" + modelName + '\'' +
                ", tokenUsage=" + tokenUsage +
                ", finishReason=" + finishReason +
                '}';
    }

    /**
     * Returns a new builder.
     *
     * @return a new builder
     */
    public static Builder<?> builder() {
        return new Builder<>();
    }

    /**
     * Builder for {@link ChatResponseMetadata}.
     *
     * @param <T> the type of the builder
     */
    public static class Builder<T extends Builder<T>> {

        private String id;
        private String modelName;
        private TokenUsage tokenUsage;
        private FinishReason finishReason;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Sets the id.
         *
         * @param id the id
         * @return the builder
         */
        public T id(String id) {
            this.id = id;
            return (T) this;
        }

        /**
         * Sets the model name.
         *
         * @param modelName the model name
         * @return the builder
         */
        public T modelName(String modelName) {
            this.modelName = modelName;
            return (T) this;
        }

        /**
         * Sets the token usage.
         *
         * @param tokenUsage the token usage
         * @return the builder
         */
        public T tokenUsage(TokenUsage tokenUsage) {
            this.tokenUsage = tokenUsage;
            return (T) this;
        }

        /**
         * Sets the finish reason.
         *
         * @param finishReason the finish reason
         * @return the builder
         */
        public T finishReason(FinishReason finishReason) {
            this.finishReason = finishReason;
            return (T) this;
        }

        /**
         * Builds the chat response metadata.
         *
         * @return the chat response metadata
         */
        public ChatResponseMetadata build() {
            return new ChatResponseMetadata(this);
        }
    }
}
