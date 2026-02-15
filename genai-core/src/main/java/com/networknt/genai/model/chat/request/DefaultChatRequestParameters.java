package com.networknt.genai.model.chat.request;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.model.chat.request.ResponseFormatType.JSON;
import static java.util.Arrays.asList;

import com.networknt.genai.tool.ToolSpecification;
import com.networknt.genai.internal.JacocoIgnoreCoverageGenerated;
import com.networknt.genai.model.chat.request.json.JsonSchema;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of {@link ChatRequestParameters}.
 */
public class DefaultChatRequestParameters implements ChatRequestParameters {

    /**
     * An empty instance.
     */
    public static final ChatRequestParameters EMPTY =
            DefaultChatRequestParameters.builder().build();

    private final String modelName;
    private final Double temperature;
    private final Double topP;
    private final Integer topK;
    private final Double frequencyPenalty;
    private final Double presencePenalty;
    private final Integer maxOutputTokens;
    private final List<String> stopSequences;
    private final List<ToolSpecification> toolSpecifications;
    private final ToolChoice toolChoice;
    private final ResponseFormat responseFormat;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    protected DefaultChatRequestParameters(Builder<?> builder) {
        this.modelName = builder.modelName;
        this.temperature = builder.temperature;
        this.topP = builder.topP;
        this.topK = builder.topK;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.presencePenalty = builder.presencePenalty;
        this.maxOutputTokens = builder.maxOutputTokens;
        this.stopSequences = copy(builder.stopSequences);
        this.toolSpecifications = copy(builder.toolSpecifications);
        this.toolChoice = builder.toolChoice;
        this.responseFormat = builder.responseFormat;
    }

    @Override
    public String modelName() {
        return modelName;
    }

    @Override
    public Double temperature() {
        return temperature;
    }

    @Override
    public Double topP() {
        return topP;
    }

    @Override
    public Integer topK() {
        return topK;
    }

    @Override
    public Double frequencyPenalty() {
        return frequencyPenalty;
    }

    @Override
    public Double presencePenalty() {
        return presencePenalty;
    }

    @Override
    public Integer maxOutputTokens() {
        return maxOutputTokens;
    }

    @Override
    public List<String> stopSequences() {
        return stopSequences;
    }

    @Override
    public List<ToolSpecification> toolSpecifications() {
        return toolSpecifications;
    }

    @Override
    public ToolChoice toolChoice() {
        return toolChoice;
    }

    @Override
    public ResponseFormat responseFormat() {
        return responseFormat;
    }

    @Override
    public ChatRequestParameters overrideWith(ChatRequestParameters that) {
        return DefaultChatRequestParameters.builder()
                .overrideWith(this)
                .overrideWith(that)
                .build();
    }

    @Override
    public ChatRequestParameters defaultedBy(ChatRequestParameters that) {
        return DefaultChatRequestParameters.builder()
                .overrideWith(that)
                .overrideWith(this)
                .build();
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultChatRequestParameters that = (DefaultChatRequestParameters) o;
        return Objects.equals(modelName, that.modelName)
                && Objects.equals(temperature, that.temperature)
                && Objects.equals(topP, that.topP)
                && Objects.equals(topK, that.topK)
                && Objects.equals(frequencyPenalty, that.frequencyPenalty)
                && Objects.equals(presencePenalty, that.presencePenalty)
                && Objects.equals(maxOutputTokens, that.maxOutputTokens)
                && Objects.equals(stopSequences, that.stopSequences)
                && Objects.equals(toolSpecifications, that.toolSpecifications)
                && Objects.equals(toolChoice, that.toolChoice)
                && Objects.equals(responseFormat, that.responseFormat);
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public int hashCode() {
        return Objects.hash(
                modelName,
                temperature,
                topP,
                topK,
                frequencyPenalty,
                presencePenalty,
                maxOutputTokens,
                stopSequences,
                toolSpecifications,
                toolChoice,
                responseFormat);
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public String toString() {
        return "DefaultChatRequestParameters{" + "modelName='"
                + modelName + '\'' + ", temperature="
                + temperature + ", topP="
                + topP + ", topK="
                + topK + ", frequencyPenalty="
                + frequencyPenalty + ", presencePenalty="
                + presencePenalty + ", maxOutputTokens="
                + maxOutputTokens + ", stopSequences="
                + stopSequences + ", toolSpecifications="
                + toolSpecifications + ", toolChoice="
                + toolChoice + ", responseFormat="
                + responseFormat + '}';
    }

    /**
     * Creates a new builder.
     *
     * @return the builder
     */
    public static Builder<?> builder() {
        return new Builder<>();
    }

    /**
     * Builder for {@link DefaultChatRequestParameters}.
     *
     * @param <T> the type of the builder
     */
    public static class Builder<T extends Builder<T>> {

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

        public Builder() {}

        /**
         * Sets the values from the given parameters.
         *
         * @param parameters the parameters
         * @return the builder
         */
        public T overrideWith(ChatRequestParameters parameters) {
            modelName(getOrDefault(parameters.modelName(), modelName));
            temperature(getOrDefault(parameters.temperature(), temperature));
            topP(getOrDefault(parameters.topP(), topP));
            topK(getOrDefault(parameters.topK(), topK));
            frequencyPenalty(getOrDefault(parameters.frequencyPenalty(), frequencyPenalty));
            presencePenalty(getOrDefault(parameters.presencePenalty(), presencePenalty));
            maxOutputTokens(getOrDefault(parameters.maxOutputTokens(), maxOutputTokens));
            stopSequences(getOrDefault(parameters.stopSequences(), stopSequences));
            toolSpecifications(getOrDefault(parameters.toolSpecifications(), toolSpecifications));
            toolChoice(getOrDefault(parameters.toolChoice(), toolChoice));
            responseFormat(getOrDefault(parameters.responseFormat(), responseFormat));
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
         * Sets the temperature.
         *
         * @param temperature the temperature
         * @return the builder
         */
        public T temperature(Double temperature) {
            this.temperature = temperature;
            return (T) this;
        }

        /**
         * Sets the top P.
         *
         * @param topP the top P
         * @return the builder
         */
        public T topP(Double topP) {
            this.topP = topP;
            return (T) this;
        }

        /**
         * Sets the top K.
         *
         * @param topK the top K
         * @return the builder
         */
        public T topK(Integer topK) {
            this.topK = topK;
            return (T) this;
        }

        /**
         * Sets the frequency penalty.
         *
         * @param frequencyPenalty the frequency penalty
         * @return the builder
         */
        public T frequencyPenalty(Double frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return (T) this;
        }

        /**
         * Sets the presence penalty.
         *
         * @param presencePenalty the presence penalty
         * @return the builder
         */
        public T presencePenalty(Double presencePenalty) {
            this.presencePenalty = presencePenalty;
            return (T) this;
        }

        /**
         * Sets the max output tokens.
         *
         * @param maxOutputTokens the max output tokens
         * @return the builder
         */
        public T maxOutputTokens(Integer maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
            return (T) this;
        }

        /**
         * Sets the stop sequences.
         *
         * @param stopSequences the stop sequences
         * @return the builder
         * @see #stopSequences(String...)
         */
        public T stopSequences(List<String> stopSequences) {
            this.stopSequences = stopSequences;
            return (T) this;
        }

        /**
         * Sets the stop sequences.
         *
         * @param stopSequences the stop sequences
         * @return the builder
         * @see #stopSequences(List)
         */
        public T stopSequences(String... stopSequences) {
            return stopSequences(asList(stopSequences));
        }

        /**
         * Sets the tool specifications.
         *
         * @param toolSpecifications the tool specifications
         * @return the builder
         * @see #toolSpecifications(ToolSpecification...)
         */
        public T toolSpecifications(List<ToolSpecification> toolSpecifications) {
            this.toolSpecifications = toolSpecifications;
            return (T) this;
        }

        /**
         * Sets the tool specifications.
         *
         * @param toolSpecifications the tool specifications
         * @return the builder
         * @see #toolSpecifications(List)
         */
        public T toolSpecifications(ToolSpecification... toolSpecifications) {
            return toolSpecifications(asList(toolSpecifications));
        }

        /**
         * Sets the tool choice.
         *
         * @param toolChoice the tool choice
         * @return the builder
         */
        public T toolChoice(ToolChoice toolChoice) {
            this.toolChoice = toolChoice;
            return (T) this;
        }

        /**
         * Sets the response format.
         *
         * @param responseFormat the response format
         * @return the builder
         * @see #responseFormat(JsonSchema)
         */
        public T responseFormat(ResponseFormat responseFormat) {
            this.responseFormat = responseFormat;
            return (T) this;
        }

        /**
         * Sets the response format using a JSON schema.
         *
         * @param jsonSchema the JSON schema
         * @return the builder
         * @see #responseFormat(ResponseFormat)
         */
        public T responseFormat(JsonSchema jsonSchema) {
            if (jsonSchema != null) {
                ResponseFormat responseFormat = ResponseFormat.builder()
                        .type(JSON)
                        .jsonSchema(jsonSchema)
                        .build();
                return responseFormat(responseFormat);
            }
            return (T) this;
        }

        /**
         * Builds the parameters.
         *
         * @return the parameters
         */
        public ChatRequestParameters build() {
            return new DefaultChatRequestParameters(this);
        }
    }
}
