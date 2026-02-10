package com.networknt.model.ollama;

import static com.networknt.agent.client.HttpMethod.DELETE;
import static com.networknt.agent.client.HttpMethod.GET;
import static com.networknt.agent.client.HttpMethod.POST;
import static com.networknt.agent.client.sse.ServerSentEventParsingHandleUtils.toStreamingHandle;
import static com.networknt.agent.internal.InternalStreamingChatResponseHandlerUtils.onCompleteResponse;
import static com.networknt.agent.internal.InternalStreamingChatResponseHandlerUtils.onCompleteToolCall;
import static com.networknt.agent.internal.InternalStreamingChatResponseHandlerUtils.onPartialResponse;
import static com.networknt.agent.internal.InternalStreamingChatResponseHandlerUtils.onPartialThinking;
import static com.networknt.agent.internal.InternalStreamingChatResponseHandlerUtils.withLoggingExceptions;
import static com.networknt.agent.internal.Utils.getOrDefault;
import static com.networknt.agent.internal.Utils.isNotNullOrEmpty;
import static com.networknt.agent.internal.Utils.isNullOrEmpty;
import static com.networknt.agent.internal.ValidationUtils.ensureNotBlank;
import static com.networknt.agent.internal.ValidationUtils.ensureNotEmpty;
import static com.networknt.model.ollama.InternalOllamaHelper.toOllamaChatRequest;
import static com.networknt.model.ollama.OllamaJsonUtils.fromJson;
import static com.networknt.model.ollama.OllamaJsonUtils.toJson;
import static com.networknt.model.ollama.OllamaJsonUtils.toJsonWithoutIdent;
import static java.lang.Boolean.TRUE;
import static java.time.Duration.ofSeconds;

import com.networknt.agent.client.HttpClient;
import com.networknt.agent.client.HttpClientBuilder;
import com.networknt.agent.client.HttpClientBuilderLoader;
import com.networknt.agent.client.HttpRequest;
import com.networknt.agent.client.SuccessfulHttpResponse;
import com.networknt.agent.client.log.LoggingHttpClient;
import com.networknt.agent.client.sse.CancellationUnsupportedHandle;
import com.networknt.agent.client.sse.ServerSentEvent;
import com.networknt.agent.client.sse.ServerSentEventContext;
import com.networknt.agent.client.sse.ServerSentEventListener;
import com.networknt.agent.internal.ExceptionMapper;
import com.networknt.agent.internal.ToolCallBuilder;
import com.networknt.agent.model.StreamingResponseHandler;
import com.networknt.agent.model.chat.request.ChatRequest;
import com.networknt.agent.model.chat.response.ChatResponse;
import com.networknt.agent.model.chat.response.StreamingChatResponseHandler;
import com.networknt.agent.model.chat.response.StreamingHandle;
import com.networknt.agent.model.output.Response;
import com.networknt.agent.model.output.TokenUsage;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;

class OllamaClient {

    private final HttpClient httpClient;
    private final String baseUrl;
    private final Supplier<Map<String, String>> customHeadersSupplier;

    OllamaClient(Builder builder) {

        HttpClientBuilder httpClientBuilder =
                getOrDefault(builder.httpClientBuilder, HttpClientBuilderLoader::loadHttpClientBuilder);

        HttpClient httpClient = httpClientBuilder
                .connectTimeout(
                        getOrDefault(getOrDefault(builder.timeout, httpClientBuilder.connectTimeout()), ofSeconds(15)))
                .readTimeout(
                        getOrDefault(getOrDefault(builder.timeout, httpClientBuilder.readTimeout()), ofSeconds(60)))
                .build();

        if (builder.logRequests || builder.logResponses) {
            this.httpClient =
                    new LoggingHttpClient(httpClient, builder.logRequests, builder.logResponses, builder.logger);
        } else {
            this.httpClient = httpClient;
        }

        this.baseUrl = ensureNotBlank(builder.baseUrl, "baseUrl");
        this.customHeadersSupplier = getOrDefault(builder.customHeadersSupplier, () -> Map::of);
    }

    private Map<String, String> buildRequestHeaders() {
        Map<String, String> dynamicHeaders = customHeadersSupplier.get();
        if (isNullOrEmpty(dynamicHeaders)) {
            return Map.of();
        }
        return dynamicHeaders;
    }

    static Builder builder() {
        return new Builder();
    }

    CompletionResponse completion(CompletionRequest request) {

        HttpRequest httpRequest = HttpRequest.builder()
                .method(POST)
                .url(baseUrl, "api/generate")
                .addHeader("Content-Type", "application/json")
                .addHeaders(buildRequestHeaders())
                .body(toJson(request))
                .build();

        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);

        return fromJson(successfulHttpResponse.body(), CompletionResponse.class);
    }

    OllamaChatResponse chat(OllamaChatRequest request) {

        HttpRequest httpRequest = HttpRequest.builder()
                .method(POST)
                .url(baseUrl, "api/chat")
                .addHeader("Content-Type", "application/json")
                .addHeaders(buildRequestHeaders())
                .body(toJson(request))
                .build();

        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);

        return fromJson(successfulHttpResponse.body(), OllamaChatResponse.class);
    }

    void streamingCompletion(CompletionRequest request, StreamingResponseHandler<String> handler) {

        HttpRequest httpRequest = HttpRequest.builder()
                .method(POST)
                .url(baseUrl, "api/generate")
                .addHeaders(buildRequestHeaders())
                .body(toJson(request))
                .build();

        httpClient.execute(httpRequest, new OllamaServerSentEventParser(), new ServerSentEventListener() {

            final StringBuilder contentBuilder = new StringBuilder();

            @Override
            public void onEvent(ServerSentEvent event) {

                CompletionResponse completionResponse = fromJson(event.data(), CompletionResponse.class);
                contentBuilder.append(completionResponse.getResponse());
                handler.onNext(completionResponse.getResponse());

                if (TRUE.equals(completionResponse.getDone())) {
                    Response<String> response = Response.from(
                            contentBuilder.toString(),
                            new TokenUsage(completionResponse.getPromptEvalCount(), completionResponse.getEvalCount()));
                    handler.onComplete(response);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                handler.onError(ExceptionMapper.DEFAULT.mapException(throwable));
            }
        });
    }

    void streamingChat(ChatRequest request, boolean returnThinking, StreamingChatResponseHandler handler) {
        ensureNotEmpty(request.messages(), "messages");

        OllamaChatRequest ollamaChatRequest = toOllamaChatRequest(request, true);

        HttpRequest httpRequest = HttpRequest.builder()
                .method(POST)
                .url(baseUrl, "api/chat")
                .addHeaders(buildRequestHeaders())
                .body(toJson(ollamaChatRequest))
                .build();

        httpClient.execute(httpRequest, new OllamaServerSentEventParser(), new ServerSentEventListener() {

            final ToolCallBuilder toolCallBuilder = new ToolCallBuilder();
            final OllamaStreamingResponseBuilder responseBuilder =
                    new OllamaStreamingResponseBuilder(toolCallBuilder, returnThinking);
            volatile StreamingHandle streamingHandle;

            @Override
            public void onEvent(ServerSentEvent event) {
                onEvent(event, new ServerSentEventContext(new CancellationUnsupportedHandle()));
            }

            @Override
            public void onEvent(ServerSentEvent event, ServerSentEventContext context) {
                if (streamingHandle == null) {
                    streamingHandle = toStreamingHandle(context.parsingHandle());
                }

                OllamaChatResponse ollamaChatResponse = fromJson(event.data(), OllamaChatResponse.class);
                responseBuilder.append(ollamaChatResponse);

                Message message = ollamaChatResponse.getMessage();
                if (message == null) {
                    return;
                }

                String content = message.getContent();
                if (!isNullOrEmpty(content)) {
                    onPartialResponse(handler, content, streamingHandle);
                }

                String thinking = message.getThinking();
                if (returnThinking && !isNullOrEmpty(thinking)) {
                    onPartialThinking(handler, thinking, streamingHandle);
                }

                List<ToolCall> toolCalls = message.getToolCalls();
                if (toolCalls != null) {
                    for (ToolCall toolCall : toolCalls) {

                        int index = getOrDefault(toolCall.getFunction().getIndex(), 0);
                        if (toolCallBuilder.index() != index) {
                            onCompleteToolCall(handler, toolCallBuilder.buildAndReset());
                            toolCallBuilder.updateIndex(index);
                        }

                        toolCallBuilder.updateName(toolCall.getFunction().getName());

                        String partialArguments =
                                toJsonWithoutIdent(toolCall.getFunction().getArguments());
                        if (isNotNullOrEmpty(partialArguments)) {
                            toolCallBuilder.appendArguments(partialArguments);
                        }
                    }
                }

                if (TRUE.equals(ollamaChatResponse.getDone())) {
                    if (toolCallBuilder.hasRequests()) {
                        onCompleteToolCall(handler, toolCallBuilder.buildAndReset());
                    }

                    ChatResponse completeResponse = responseBuilder.build(ollamaChatResponse);
                    onCompleteResponse(handler, completeResponse);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                RuntimeException mappedException = ExceptionMapper.DEFAULT.mapException(throwable);
                withLoggingExceptions(() -> handler.onError(mappedException));
            }
        });
    }

    EmbeddingResponse embed(EmbeddingRequest request) {

        HttpRequest httpRequest = HttpRequest.builder()
                .method(POST)
                .url(baseUrl, "api/embed")
                .addHeader("Content-Type", "application/json")
                .addHeaders(buildRequestHeaders())
                .body(toJson(request))
                .build();

        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);

        return fromJson(successfulHttpResponse.body(), EmbeddingResponse.class);
    }

    ModelsListResponse listModels() {

        HttpRequest httpRequest = HttpRequest.builder()
                .method(GET)
                .url(baseUrl, "api/tags")
                .addHeader("Content-Type", "application/json")
                .addHeaders(buildRequestHeaders())
                .build();

        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);

        return fromJson(successfulHttpResponse.body(), ModelsListResponse.class);
    }

    OllamaModelCard showInformation(ShowModelInformationRequest showInformationRequest) {

        HttpRequest httpRequest = HttpRequest.builder()
                .method(POST)
                .url(baseUrl, "api/show")
                .addHeader("Content-Type", "application/json")
                .addHeaders(buildRequestHeaders())
                .body(toJson(showInformationRequest))
                .build();

        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);

        return fromJson(successfulHttpResponse.body(), OllamaModelCard.class);
    }

    RunningModelsListResponse listRunningModels() {

        HttpRequest httpRequest = HttpRequest.builder()
                .method(GET)
                .url(baseUrl, "api/ps")
                .addHeader("Content-Type", "application/json")
                .addHeaders(buildRequestHeaders())
                .build();

        SuccessfulHttpResponse successfulHttpResponse = httpClient.execute(httpRequest);

        return fromJson(successfulHttpResponse.body(), RunningModelsListResponse.class);
    }

    Void deleteModel(DeleteModelRequest deleteModelRequest) {

        HttpRequest httpRequest = HttpRequest.builder()
                .method(DELETE)
                .url(baseUrl, "api/delete")
                .addHeader("Content-Type", "application/json")
                .addHeaders(buildRequestHeaders())
                .body(toJson(deleteModelRequest))
                .build();

        httpClient.execute(httpRequest);

        return null;
    }

    static class Builder {

        private HttpClientBuilder httpClientBuilder;
        private String baseUrl;
        private Duration timeout;
        private boolean logRequests;
        private boolean logResponses;
        private Logger logger;
        private Supplier<Map<String, String>> customHeadersSupplier;

        /**
         * Sets the {@link HttpClientBuilder} that will be used to create the {@link HttpClient}
         * that will be used to communicate with Ollama.
         * <p>
         * NOTE: {@link #timeout(Duration)} overrides timeouts set on the {@link HttpClientBuilder}.
         */
        Builder httpClientBuilder(HttpClientBuilder httpClientBuilder) {
            this.httpClientBuilder = httpClientBuilder;
            return this;
        }

        Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        Builder logRequests(Boolean logRequests) {
            if (logRequests == null) {
                logRequests = false;
            }
            this.logRequests = logRequests;
            return this;
        }

        Builder logResponses(Boolean logResponses) {
            if (logResponses == null) {
                logResponses = false;
            }
            this.logResponses = logResponses;
            return this;
        }

        /**
         * @param logger an alternate {@link Logger} to be used instead of the default one provided by Langchain4J for logging requests and responses.
         * @return {@code this}.
         */
        Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }

        Builder customHeaders(Map<String, String> customHeaders) {
            this.customHeadersSupplier = () -> customHeaders;
            return this;
        }

        Builder customHeaders(Supplier<Map<String, String>> customHeadersSupplier) {
            this.customHeadersSupplier = customHeadersSupplier;
            return this;
        }

        OllamaClient build() {
            return new OllamaClient(this);
        }
    }
}
