package com.networknt.genai.model.openai;

import static com.networknt.genai.internal.RetryUtils.withRetryMappingExceptions;
import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.model.openai.internal.OpenAiUtils.DEFAULT_OPENAI_URL;
import static com.networknt.genai.model.openai.internal.OpenAiUtils.DEFAULT_USER_AGENT;
import static com.networknt.genai.spi.ServiceHelper.loadFactories;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.singletonList;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.SystemMessage;
import com.networknt.genai.data.message.ToolExecutionResultMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.client.HttpClientBuilder;
import com.networknt.genai.model.moderation.Moderation;
import com.networknt.genai.model.moderation.ModerationModel;
import com.networknt.genai.model.openai.internal.OpenAiClient;
import com.networknt.genai.model.openai.internal.moderation.ModerationRequest;
import com.networknt.genai.model.openai.internal.moderation.ModerationResponse;
import com.networknt.genai.model.openai.internal.moderation.ModerationResult;
import com.networknt.genai.model.openai.spi.OpenAiModerationModelBuilderFactory;
import com.networknt.genai.model.output.Response;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;

/**
 * Represents an OpenAI moderation model, such as text-moderation-latest.
 */
public class OpenAiModerationModel implements ModerationModel {

    private final OpenAiClient client;
    private final String modelName;
    private final Integer maxRetries;

    public OpenAiModerationModel(OpenAiModerationModelBuilder builder) {

        this.client = OpenAiClient.builder()
                .httpClientBuilder(builder.httpClientBuilder)
                .baseUrl(getOrDefault(builder.baseUrl, DEFAULT_OPENAI_URL))
                .apiKey(builder.apiKey)
                .organizationId(builder.organizationId)
                .projectId(builder.projectId)
                .connectTimeout(getOrDefault(builder.timeout, ofSeconds(15)))
                .readTimeout(getOrDefault(builder.timeout, ofSeconds(60)))
                .logRequests(getOrDefault(builder.logRequests, false))
                .logResponses(getOrDefault(builder.logResponses, false))
                .logger(builder.logger)
                .userAgent(DEFAULT_USER_AGENT)
                .customHeaders(builder.customHeadersSupplier)
                .customQueryParams(builder.customQueryParams)
                .build();
        this.modelName = builder.modelName;
        this.maxRetries = getOrDefault(builder.maxRetries, 2);
    }

    public String modelName() {
        return modelName;
    }

    @Override
    public Response<Moderation> moderate(String text) {
        return moderateInternal(singletonList(text));
    }

    private Response<Moderation> moderateInternal(List<String> inputs) {

        ModerationRequest request =
                ModerationRequest.builder().model(modelName).input(inputs).build();

        ModerationResponse response =
                withRetryMappingExceptions(() -> client.moderation(request).execute(), maxRetries);

        int i = 0;
        for (ModerationResult moderationResult : response.results()) {
            if (Boolean.TRUE.equals(moderationResult.isFlagged())) {
                return Response.from(Moderation.flagged(inputs.get(i)));
            }
            i++;
        }

        return Response.from(Moderation.notFlagged());
    }

    @Override
    public Response<Moderation> moderate(List<ChatMessage> messages) {
        List<String> inputs =
                messages.stream().map(OpenAiModerationModel::toText).toList();

        return moderateInternal(inputs);
    }

    private static String toText(ChatMessage chatMessage) {
        if (chatMessage instanceof SystemMessage systemMessage) {
            return systemMessage.text();
        } else if (chatMessage instanceof UserMessage userMessage) {
            return userMessage.singleText();
        } else if (chatMessage instanceof AiMessage aiMessage) {
            return aiMessage.text();
        } else if (chatMessage instanceof ToolExecutionResultMessage toolExecutionResultMessage) {
            return toolExecutionResultMessage.text();
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + chatMessage.type());
        }
    }

    public static OpenAiModerationModelBuilder builder() {
        for (OpenAiModerationModelBuilderFactory factory : loadFactories(OpenAiModerationModelBuilderFactory.class)) {
            return factory.get();
        }
        return new OpenAiModerationModelBuilder();
    }

    public static class OpenAiModerationModelBuilder {

        private HttpClientBuilder httpClientBuilder;
        private String baseUrl;
        private String apiKey;
        private String organizationId;
        private String projectId;

        private String modelName;
        private Duration timeout;
        private Integer maxRetries;
        private Boolean logRequests;
        private Boolean logResponses;
        private Logger logger;
        private Supplier<Map<String, String>> customHeadersSupplier;
        private Map<String, String> customQueryParams;

        public OpenAiModerationModelBuilder() {
            // This is public so it can be extended
        }

        public OpenAiModerationModelBuilder httpClientBuilder(HttpClientBuilder httpClientBuilder) {
            this.httpClientBuilder = httpClientBuilder;
            return this;
        }

        public OpenAiModerationModelBuilder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public OpenAiModerationModelBuilder modelName(OpenAiModerationModelName modelName) {
            this.modelName = modelName.toString();
            return this;
        }

        public OpenAiModerationModelBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public OpenAiModerationModelBuilder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public OpenAiModerationModelBuilder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public OpenAiModerationModelBuilder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public OpenAiModerationModelBuilder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public OpenAiModerationModelBuilder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public OpenAiModerationModelBuilder logRequests(Boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        public OpenAiModerationModelBuilder logResponses(Boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        /**
         * @param logger an alternate {@link Logger} to be used instead of the default one provided by Langchain4J for logging requests and responses.
         * @return {@code this}.
         */
        public OpenAiModerationModelBuilder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Sets custom HTTP headers.
         */
        public OpenAiModerationModelBuilder customHeaders(Map<String, String> customHeaders) {
            this.customHeadersSupplier = () -> customHeaders;
            return this;
        }

        /**
         * Sets a supplier for custom HTTP headers.
         * The supplier is called before each request, allowing dynamic header values.
         * For example, this is useful for OAuth2 tokens that expire and need refreshing.
         */
        public OpenAiModerationModelBuilder customHeaders(Supplier<Map<String, String>> customHeadersSupplier) {
            this.customHeadersSupplier = customHeadersSupplier;
            return this;
        }

        public OpenAiModerationModelBuilder customQueryParams(Map<String, String> customQueryParams) {
            this.customQueryParams = customQueryParams;
            return this;
        }

        public OpenAiModerationModel build() {
            return new OpenAiModerationModel(this);
        }
    }
}
