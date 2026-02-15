package com.networknt.genai.model.ollama;

import static com.networknt.genai.internal.RetryUtils.withRetryMappingExceptions;
import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureNotBlank;
import static com.networknt.genai.model.ollama.InternalOllamaHelper.toOllamaResponseFormat;
import static com.networknt.genai.spi.ServiceHelper.loadFactories;

import com.networknt.genai.client.HttpClient;
import com.networknt.genai.client.HttpClientBuilder;
import com.networknt.genai.model.chat.request.ResponseFormat;
import com.networknt.genai.model.language.LanguageModel;
import com.networknt.genai.model.ollama.spi.OllamaLanguageModelBuilderFactory;
import com.networknt.genai.model.output.Response;
import com.networknt.genai.model.output.TokenUsage;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <a href="https://github.com/jmorganca/ollama/blob/main/docs/api.md">Ollama API reference</a>
 * <br>
 * <a href="https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values">Ollama API parameters</a>.
 */
public class OllamaLanguageModel implements LanguageModel {

    private final OllamaClient client;
    private final String modelName;
    private final Options options;
    private final ResponseFormat responseFormat;
    private final Integer maxRetries;

    public OllamaLanguageModel(OllamaLanguageModelBuilder builder) {
        this.client = OllamaClient.builder()
                .httpClientBuilder(builder.httpClientBuilder)
                .baseUrl(builder.baseUrl)
                .timeout(builder.timeout)
                .logRequests(builder.logRequests)
                .logResponses(builder.logResponses)
                .customHeaders(builder.customHeadersSupplier)
                .build();
        this.modelName = ensureNotBlank(builder.modelName, "modelName");
        this.options = Options.builder()
                .temperature(builder.temperature)
                .topK(builder.topK)
                .topP(builder.topP)
                .repeatPenalty(builder.repeatPenalty)
                .seed(builder.seed)
                .numPredict(builder.numPredict)
                .numCtx(builder.numCtx)
                .stop(builder.stop)
                .build();
        this.responseFormat = builder.responseFormat;
        this.maxRetries = getOrDefault(builder.maxRetries, 2);
    }

    public static OllamaLanguageModelBuilder builder() {
        for (OllamaLanguageModelBuilderFactory factory : loadFactories(OllamaLanguageModelBuilderFactory.class)) {
            return factory.get();
        }
        return new OllamaLanguageModelBuilder();
    }

    @Override
    public Response<String> generate(String prompt) {

        CompletionRequest request = CompletionRequest.builder()
                .model(modelName)
                .prompt(prompt)
                .options(options)
                .format(toOllamaResponseFormat(responseFormat))
                .stream(false)
                .build();

        CompletionResponse response = withRetryMappingExceptions(() -> client.completion(request), maxRetries);

        return Response.from(
                response.getResponse(), new TokenUsage(response.getPromptEvalCount(), response.getEvalCount()));
    }

    public static class OllamaLanguageModelBuilder {

        private HttpClientBuilder httpClientBuilder;
        private String baseUrl;
        private String modelName;
        private Double temperature;
        private Integer topK;
        private Double topP;
        private Double repeatPenalty;
        private Integer seed;
        private Integer numPredict;
        private Integer numCtx;
        private List<String> stop;
        private ResponseFormat responseFormat;
        private Duration timeout;
        private Integer maxRetries;
        private Boolean logRequests;
        private Boolean logResponses;
        private Supplier<Map<String, String>> customHeadersSupplier;

        public OllamaLanguageModelBuilder() {
            // This is public so it can be extended
        }

        /**
         * Sets the {@link HttpClientBuilder} that will be used to create the {@link HttpClient}
         * that will be used to communicate with Ollama.
         * <p>
         * NOTE: {@link #timeout(Duration)} overrides timeouts set on the {@link HttpClientBuilder}.
         */
        public OllamaLanguageModelBuilder httpClientBuilder(HttpClientBuilder httpClientBuilder) {
            this.httpClientBuilder = httpClientBuilder;
            return this;
        }

        public OllamaLanguageModelBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public OllamaLanguageModelBuilder modelName(String modelName) {
            this.modelName = modelName;
            return this;
        }

        public OllamaLanguageModelBuilder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public OllamaLanguageModelBuilder topK(Integer topK) {
            this.topK = topK;
            return this;
        }

        public OllamaLanguageModelBuilder topP(Double topP) {
            this.topP = topP;
            return this;
        }

        public OllamaLanguageModelBuilder repeatPenalty(Double repeatPenalty) {
            this.repeatPenalty = repeatPenalty;
            return this;
        }

        public OllamaLanguageModelBuilder seed(Integer seed) {
            this.seed = seed;
            return this;
        }

        public OllamaLanguageModelBuilder numPredict(Integer numPredict) {
            this.numPredict = numPredict;
            return this;
        }

        public OllamaLanguageModelBuilder numCtx(Integer numCtx) {
            this.numCtx = numCtx;
            return this;
        }

        public OllamaLanguageModelBuilder stop(List<String> stop) {
            this.stop = stop;
            return this;
        }

        public OllamaLanguageModelBuilder responseFormat(ResponseFormat responseFormat) {
            this.responseFormat = responseFormat;
            return this;
        }

        public OllamaLanguageModelBuilder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public OllamaLanguageModelBuilder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        public OllamaLanguageModelBuilder logRequests(Boolean logRequests) {
            this.logRequests = logRequests;
            return this;
        }

        public OllamaLanguageModelBuilder logResponses(Boolean logResponses) {
            this.logResponses = logResponses;
            return this;
        }

        /**
         * Sets custom HTTP headers.
         */
        public OllamaLanguageModelBuilder customHeaders(Map<String, String> customHeaders) {
            this.customHeadersSupplier = () -> customHeaders;
            return this;
        }

        /**
         * Sets a supplier for custom HTTP headers.
         * The supplier is called before each request, allowing dynamic header values.
         * For example, this is useful for OAuth2 tokens that expire and need refreshing.
         */
        public OllamaLanguageModelBuilder customHeaders(Supplier<Map<String, String>> customHeadersSupplier) {
            this.customHeadersSupplier = customHeadersSupplier;
            return this;
        }

        public OllamaLanguageModel build() {
            return new OllamaLanguageModel(this);
        }
    }
}
