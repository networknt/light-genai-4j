package com.networknt.genai.model.chat.response;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.internal.JacocoIgnoreCoverageGenerated;
import com.networknt.genai.model.output.FinishReason;
import com.networknt.genai.model.output.TokenUsage;
import java.util.Objects;

/**
 * Represents a response from a chat model.
 */
public class ChatResponse {

    private final AiMessage aiMessage;
    private final ChatResponseMetadata metadata;

    /**
     * Creates a new chat response.
     *
     * @param builder the builder
     */
    protected ChatResponse(Builder builder) {
        this.aiMessage = ensureNotNull(builder.aiMessage, "aiMessage");

        ChatResponseMetadata.Builder<?> metadataBuilder = ChatResponseMetadata.builder();
        if (builder.id != null) {
            validate(builder, "id");
            metadataBuilder.id(builder.id);
        }
        if (builder.modelName != null) {
            validate(builder, "modelName");
            metadataBuilder.modelName(builder.modelName);
        }
        if (builder.tokenUsage != null) {
            validate(builder, "tokenUsage");
            metadataBuilder.tokenUsage(builder.tokenUsage);
        }
        if (builder.finishReason != null) {
            validate(builder, "finishReason");
            metadataBuilder.finishReason(builder.finishReason);
        }
        if (builder.metadata != null) {
            this.metadata = builder.metadata;
        } else {
            this.metadata = metadataBuilder.build();
        }
    }

    /**
     * Returns the AI message.
     *
     * @return the AI message
     */
    public AiMessage aiMessage() {
        return aiMessage;
    }

    /**
     * Converts the current instance of {@code ChatResponse} into a {@link Builder},
     * allowing modifications to the current object's fields.
     *
     * @return a new {@link Builder} instance initialized with the current state of this {@code ChatResponse}.
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Returns the metadata.
     *
     * @return the metadata
     */
    public ChatResponseMetadata metadata() {
        return metadata;
    }

    /**
     * Returns the id of the response.
     *
     * @return the id of the response
     */
    public String id() {
        return metadata.id();
    }

    /**
     * Returns the name of the model.
     *
     * @return the name of the model
     */
    public String modelName() {
        return metadata.modelName();
    }

    /**
     * Returns the token usage.
     *
     * @return the token usage
     */
    public TokenUsage tokenUsage() {
        return metadata.tokenUsage();
    }

    /**
     * Returns the finish reason.
     *
     * @return the finish reason
     */
    public FinishReason finishReason() {
        return metadata.finishReason();
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatResponse that = (ChatResponse) o;
        return Objects.equals(this.aiMessage, that.aiMessage) && Objects.equals(this.metadata, that.metadata);
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public int hashCode() {
        return Objects.hash(aiMessage, metadata);
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public String toString() {
        return "ChatResponse {" + " aiMessage = " + aiMessage + ", metadata = " + metadata + " }";
    }

    /**
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link ChatResponse}.
     */
    public static class Builder {
        private AiMessage aiMessage;
        private ChatResponseMetadata metadata;

        private String id;
        private String modelName;
        private TokenUsage tokenUsage;
        private FinishReason finishReason;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Creates a new builder from a chat response.
         *
         * @param chatResponse the chat response
         */
        public Builder(ChatResponse chatResponse) {
            this.aiMessage = chatResponse.aiMessage;
            this.metadata = chatResponse.metadata;
        }

        /**
         * Sets the AI message.
         *
         * @param aiMessage the AI message
         * @return the builder
         */
        public Builder aiMessage(AiMessage aiMessage) {
            this.aiMessage = aiMessage;
            return this;
        }

        /**
         * Sets the metadata.
         *
         * @param metadata the metadata
         * @return the builder
         */
        public Builder metadata(ChatResponseMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Sets the id.
         *
         * @param id the id
         * @return the builder
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * Sets the model name.
         *
         * @param modelName the model name
         * @return the builder
         */
        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        /**
         * Sets the token usage.
         *
         * @param tokenUsage the token usage
         * @return the builder
         */
        public Builder tokenUsage(TokenUsage tokenUsage) {
            this.tokenUsage = tokenUsage;
            return this;
        }

        /**
         * Sets the finish reason.
         *
         * @param finishReason the finish reason
         * @return the builder
         */
        public Builder finishReason(FinishReason finishReason) {
            this.finishReason = finishReason;
            return this;
        }

        /**
         * Builds the chat response.
         *
         * @return the chat response
         */
        public ChatResponse build() {
            return new ChatResponse(this);
        }
    }

    private static void validate(Builder builder, String name) {
        if (builder.metadata != null) {
            throw new IllegalArgumentException("Cannot set both 'metadata' and '%s' on ChatResponse".formatted(name));
        }
    }
}
