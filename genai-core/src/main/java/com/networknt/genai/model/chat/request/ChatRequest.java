package com.networknt.genai.model.chat.request;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.Utils.isNullOrEmpty;
import static com.networknt.genai.internal.ValidationUtils.ensureNotEmpty;
import static java.util.Arrays.asList;

import com.networknt.genai.tool.ToolSpecification;
import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.internal.JacocoIgnoreCoverageGenerated;

import java.util.List;
import java.util.Objects;

public class ChatRequest {

    private final List<ChatMessage> messages;
    private final ChatRequestParameters parameters;

    /**
     * Creates a new chat request.
     *
     * @param builder the builder
     */
    protected ChatRequest(Builder builder) {
        this.messages = copy(ensureNotEmpty(builder.messages, "messages"));

        DefaultChatRequestParameters.Builder<?> parametersBuilder = ChatRequestParameters.builder();

        if (builder.modelName != null) {
            validate(builder, "modelName");
            parametersBuilder.modelName(builder.modelName);
        }
        if (builder.temperature != null) {
            validate(builder, "temperature");
            parametersBuilder.temperature(builder.temperature);
        }
        if (builder.topP != null) {
            validate(builder, "topP");
            parametersBuilder.topP(builder.topP);
        }
        if (builder.topK != null) {
            validate(builder, "topK");
            parametersBuilder.topK(builder.topK);
        }
        if (builder.frequencyPenalty != null) {
            validate(builder, "frequencyPenalty");
            parametersBuilder.frequencyPenalty(builder.frequencyPenalty);
        }
        if (builder.presencePenalty != null) {
            validate(builder, "presencePenalty");
            parametersBuilder.presencePenalty(builder.presencePenalty);
        }
        if (builder.maxOutputTokens != null) {
            validate(builder, "maxOutputTokens");
            parametersBuilder.maxOutputTokens(builder.maxOutputTokens);
        }
        if (!isNullOrEmpty(builder.stopSequences)) {
            validate(builder, "stopSequences");
            parametersBuilder.stopSequences(builder.stopSequences);
        }
        if (!isNullOrEmpty(builder.toolSpecifications)) {
            validate(builder, "toolSpecifications");
            parametersBuilder.toolSpecifications(builder.toolSpecifications);
        }
        if (builder.toolChoice != null) {
            validate(builder, "toolChoice");
            parametersBuilder.toolChoice(builder.toolChoice);
        }
        if (builder.responseFormat != null) {
            validate(builder, "responseFormat");
            parametersBuilder.responseFormat(builder.responseFormat);
        }

        if (builder.parameters != null) {
            this.parameters = builder.parameters;
        } else {
            this.parameters = parametersBuilder.build();
        }
    }

    /**
     * Returns the messages.
     *
     * @return the messages
     */
    public List<ChatMessage> messages() {
        return messages;
    }

    /**
     * Returns the parameters.
     *
     * @return the parameters
     */
    public ChatRequestParameters parameters() {
        return parameters;
    }

    /**
     * Returns the model name.
     *
     * @return the model name
     */
    public String modelName() {
        return parameters.modelName();
    }

    /**
     * Returns the temperature.
     *
     * @return the temperature
     */
    public Double temperature() {
        return parameters.temperature();
    }

    /**
     * Returns the top P.
     *
     * @return the top P
     */
    public Double topP() {
        return parameters.topP();
    }

    /**
     * Returns the top K.
     *
     * @return the top K
     */
    public Integer topK() {
        return parameters.topK();
    }

    /**
     * Returns the frequency penalty.
     *
     * @return the frequency penalty
     */
    public Double frequencyPenalty() {
        return parameters.frequencyPenalty();
    }

    /**
     * Returns the presence penalty.
     *
     * @return the presence penalty
     */
    public Double presencePenalty() {
        return parameters.presencePenalty();
    }

    /**
     * Returns the max output tokens.
     *
     * @return the max output tokens
     */
    public Integer maxOutputTokens() {
        return parameters.maxOutputTokens();
    }

    /**
     * Returns the stop sequences.
     *
     * @return the stop sequences
     */
    public List<String> stopSequences() {
        return parameters.stopSequences();
    }

    /**
     * Returns the tool specifications.
     *
     * @return the tool specifications
     */
    public List<ToolSpecification> toolSpecifications() {
        return parameters.toolSpecifications();
    }

    /**
     * Returns the tool choice.
     *
     * @return the tool choice
     */
    public ToolChoice toolChoice() {
        return parameters.toolChoice();
    }

    /**
     * Returns the response format.
     *
     * @return the response format
     */
    public ResponseFormat responseFormat() {
        return parameters.responseFormat();
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRequest that = (ChatRequest) o;
        return Objects.equals(this.messages, that.messages) && Objects.equals(this.parameters, that.parameters);
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public int hashCode() {
        return Objects.hash(messages, parameters);
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public String toString() {
        return "ChatRequest {" + " messages = " + messages + ", parameters = " + parameters + " }";
    }

    /**
     * Transforms this instance to a {@link Builder} with all of the same field values
     *
     * @return a new builder with the same values
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link ChatRequest}.
     */
    public static class Builder {

        private List<ChatMessage> messages;
        private ChatRequestParameters parameters;

        private String modelName;
        private Double temperature;
        private Double topP;
        private Integer topK;
        private Double frequencyPenalty;
        private Double presencePenalty;
        private Integer maxOutputTokens;
        private List<String> stopSequences;
        private List<ToolSpecification> toolSpecifications;
        private ToolChoice toolChoice;
        private ResponseFormat responseFormat;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Creates a builder from an existing request.
         *
         * @param chatRequest the request to copy from
         */
        public Builder(ChatRequest chatRequest) {
            this.messages = chatRequest.messages;
            this.parameters = chatRequest.parameters;
        }

        public Builder messages(List<ChatMessage> messages) {
            this.messages = messages;
            return this;
        }

        public Builder messages(ChatMessage... messages) {
            return messages(asList(messages));
        }

        public Builder parameters(ChatRequestParameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder topP(Double topP) {
            this.topP = topP;
            return this;
        }

        public Builder topK(Integer topK) {
            this.topK = topK;
            return this;
        }

        public Builder frequencyPenalty(Double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder presencePenalty(Double presencePenalty) {
            this.presencePenalty = presencePenalty;
            return this;
        }

        public Builder maxOutputTokens(Integer maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
            return this;
        }

        public Builder stopSequences(List<String> stopSequences) {
            this.stopSequences = stopSequences;
            return this;
        }

        public Builder toolSpecifications(List<ToolSpecification> toolSpecifications) {
            this.toolSpecifications = toolSpecifications;
            return this;
        }

        public Builder toolSpecifications(ToolSpecification... toolSpecifications) {
            return toolSpecifications(asList(toolSpecifications));
        }

        public Builder toolChoice(ToolChoice toolChoice) {
            this.toolChoice = toolChoice;
            return this;
        }

        public Builder responseFormat(ResponseFormat responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        public ChatRequest build() {
            return new ChatRequest(this);
        }
    }

    private static void validate(Builder builder, String name) {
        if (builder.parameters != null) {
            throw new IllegalArgumentException("Cannot set both 'parameters' and '%s' on ChatRequest".formatted(name));
        }
    }
}
