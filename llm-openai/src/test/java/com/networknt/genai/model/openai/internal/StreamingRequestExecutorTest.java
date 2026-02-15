package com.networknt.genai.model.openai.internal;

import com.networknt.genai.client.HttpClient;
import com.networknt.genai.client.HttpRequest;
import com.networknt.genai.client.SuccessfulHttpResponse;
import com.networknt.genai.client.sse.ServerSentEvent;
import com.networknt.genai.client.sse.ServerSentEventListener;
import com.networknt.genai.client.sse.ServerSentEventParser;
import com.networknt.genai.model.openai.internal.chat.ChatCompletionResponse;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static com.networknt.genai.client.HttpMethod.GET;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class StreamingRequestExecutorTest {

    private static final String ERROR_MESSAGE =
            "{\"error\":{\"message\":\"Failed to call a function. Please adjust your prompt. " +
                    "See 'failed_generation' for more details.\",\"type\":\"invalid_request_error\"," +
                    "\"code\":\"tool_use_failed\"," +
                    "\"failed_generation\":\"Tool use failed: no tool can be called with name getCarsList\"," +
                    "\"status_code\":400}}";

    @Test
    void should_process_streaming_error() throws Exception {

        HttpClient httpClient = new HttpClient() {

            @Override
            public SuccessfulHttpResponse execute(HttpRequest request) {
                throw new IllegalStateException("this method should not be called");
            }

            @Override
            public void execute(HttpRequest request, ServerSentEventParser parser, ServerSentEventListener listener) {
                listener.onEvent(new ServerSentEvent("error", ERROR_MESSAGE));
            }
        };

        HttpRequest streamingHttpRequest = HttpRequest.builder()
                .method(GET)
                .url("http://does.not.matter")
                .build();

        StreamingRequestExecutor<ChatCompletionResponse> executor =
                new StreamingRequestExecutor<>(httpClient, streamingHttpRequest, ChatCompletionResponse.class);

        CompletableFuture<Throwable> futureError = new CompletableFuture<>();

        executor.onPartialResponse(ignored -> {
                })
                .onError(futureError::complete)
                .execute();

        Throwable error = futureError.get(30, SECONDS);

        assertThat(error)
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessage(ERROR_MESSAGE);
    }
}
