package com.networknt.genai.model.openai;

import static com.networknt.genai.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import com.networknt.genai.exception.AuthenticationException;
import com.networknt.genai.exception.HttpException;
import com.networknt.genai.exception.InternalServerException;
import com.networknt.genai.exception.InvalidRequestException;
import com.networknt.genai.exception.LangChain4jException;
import com.networknt.genai.exception.ModelNotFoundException;
import com.networknt.genai.exception.RateLimitException;
import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.chat.response.StreamingChatResponseHandler;
import io.ktor.http.HttpStatusCode;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import me.kpavlov.aimocks.openai.MockOpenai;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class OpenAiStreamingChatModelErrorsTest {

    private static final MockOpenai MOCK = new MockOpenai();

    public static final Duration TIMEOUT = Duration.ofSeconds(3);

    StreamingChatModel model = OpenAiStreamingChatModel.builder()
            .baseUrl(MOCK.baseUrl())
            .modelName(GPT_4_O_MINI)
            .timeout(TIMEOUT)
            .logRequests(true)
            .logResponses(true)
            .build();

    public static Stream<Arguments> errors() {
        return Stream.of(
                Arguments.of(400, InvalidRequestException.class),
                Arguments.of(401, AuthenticationException.class),
                Arguments.of(403, AuthenticationException.class),
                Arguments.of(404, ModelNotFoundException.class),
                Arguments.of(413, InvalidRequestException.class),
                Arguments.of(429, RateLimitException.class),
                Arguments.of(500, InternalServerException.class),
                Arguments.of(503, InternalServerException.class));
    }

    @ParameterizedTest
    @MethodSource("errors")
    void should_handle_error_responses(int httpStatusCode, Class<LangChain4jException> exception) throws Exception {

        // given
        final var question = "Return error: " + httpStatusCode;
        MOCK.completion(req -> req.userMessageContains(question)).respondsError(res -> {
            res.setHttpStatus(HttpStatusCode.Companion.fromValue(httpStatusCode));
            res.setBody("");
        });

        CompletableFuture<Throwable> futureError = new CompletableFuture<>();
        StreamingChatResponseHandler handler = new ErrorHandler(futureError);

        // when
        model.chat(question, handler);

        // then
        Throwable error = futureError.get(30, SECONDS);

        assertThat(error).isExactlyInstanceOf(exception).satisfies(ex -> assertThat(
                        ((HttpException) ex.getCause()).statusCode())
                .as("statusCode")
                .isEqualTo(httpStatusCode));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 100})
    void should_handle_timeout(int millis) throws Exception {

        // given
        Duration timeout = Duration.ofMillis(millis);

        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(MOCK.baseUrl())
                .modelName(GPT_4_O_MINI)
                .timeout(timeout)
                .logRequests(true)
                .logResponses(true)
                .build();

        final var question = "Simulate timeout";
        MOCK.completion(req -> req.userMessageContains(question)).respondsError(res -> {
            res.delayMillis(TIMEOUT.multipliedBy(2).toMillis());
            res.setHttpStatus(HttpStatusCode.Companion.getNoContent());
            res.setBody("");
        });

        CompletableFuture<Throwable> futureError = new CompletableFuture<>();
        StreamingChatResponseHandler handler = new ErrorHandler(futureError);

        // when
        model.chat(question, handler);

        // then
        Throwable error = futureError.get(30, SECONDS);

        assertThat(error).isExactlyInstanceOf(com.networknt.genai.exception.TimeoutException.class);
    }

    private record ErrorHandler(CompletableFuture<Throwable> futureError) implements StreamingChatResponseHandler {

        @Override
        public void onPartialResponse(String partialResponse) {
            futureError.completeExceptionally(new RuntimeException("onPartialResponse must not be called"));
        }

        @Override
        public void onCompleteResponse(ChatResponse completeResponse) {
            futureError.completeExceptionally(new RuntimeException("onCompleteResponse must not be called"));
        }

        @Override
        public void onError(Throwable error) {
            futureError.complete(error);
        }
    }
}
