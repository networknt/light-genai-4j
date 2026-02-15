package com.networknt.genai.model.chat;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.model.StreamingResponseHandler;
import com.networknt.genai.model.output.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.networknt.genai.internal.Exceptions.illegalArgument;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

public class TestStreamingResponseHandler<T> implements StreamingResponseHandler<T> {

    private final CompletableFuture<Response<T>> futureResponse = new CompletableFuture<>();

    private final StringBuffer textContentBuilder = new StringBuffer();

    @Override
    public void onNext(String token) {
        textContentBuilder.append(token);
    }

    @Override
    public void onComplete(Response<T> response) {

        String expectedTextContent = textContentBuilder.toString();
        if (response.content() instanceof AiMessage aiMessage) {
            if (aiMessage.hasToolExecutionRequests()){
                assertThat(aiMessage.toolExecutionRequests().size()).isGreaterThan(0);
            } else {
                assertThat(aiMessage.text()).isEqualTo(expectedTextContent);
            }
        } else if (response.content() instanceof String) {
            assertThat(response.content()).isEqualTo(expectedTextContent);
        } else {
            throw illegalArgument("Unknown response content: " + response.content());
        }

        futureResponse.complete(response);
    }

    @Override
    public void onError(Throwable error) {
        futureResponse.completeExceptionally(error);
    }

    public Response<T> get()  {
        try {
            return futureResponse.get(30, SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
