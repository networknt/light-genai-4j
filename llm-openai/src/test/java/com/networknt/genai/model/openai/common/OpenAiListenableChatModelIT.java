package com.networknt.genai.model.openai.common;

import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.exception.AuthenticationException;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.listener.ChatModelErrorContext;
import com.networknt.genai.model.chat.listener.ChatModelListener;
import com.networknt.genai.model.chat.listener.ChatModelRequestContext;
import com.networknt.genai.model.chat.listener.ChatModelResponseContext;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.request.ChatRequestParameters;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.openai.OpenAiChatModel;
import com.networknt.genai.model.openai.OpenAiChatRequestParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiListenableChatModelIT { // TODO extract to ListenableChatModelIT

    static class TestChatModelListener implements ChatModelListener {

        AtomicInteger onRequestCalledTimes = new AtomicInteger(0);
        AtomicReference<ChatModelRequestContext> requestContextReference = new AtomicReference<>();

        AtomicInteger onResponseCalledTimes = new AtomicInteger(0);
        AtomicReference<ChatModelResponseContext> responseContextReference = new AtomicReference<>();

        AtomicInteger onErrorCalledTimes = new AtomicInteger(0);
        AtomicReference<ChatModelErrorContext> errorContextReference = new AtomicReference<>();

        @Override
        public void onRequest(ChatModelRequestContext requestContext) {
            onRequestCalledTimes.incrementAndGet();
            requestContextReference.set(requestContext);
        }

        @Override
        public void onResponse(ChatModelResponseContext responseContext) {
            onResponseCalledTimes.incrementAndGet();
            responseContextReference.set(responseContext);
        }

        @Override
        public void onError(ChatModelErrorContext errorContext) {
            onErrorCalledTimes.incrementAndGet();
            errorContextReference.set(errorContext);
        }
    }

    @Test
    void should_listen_request_and_response() {

        // given
        TestChatModelListener listener = new TestChatModelListener();

        ChatModel model = OpenAiChatModel.builder()
                .baseUrl(System.getenv("OPENAI_BASE_URL"))
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
                .modelName("gpt-4o-mini")
                .temperature(0.5)
                .user("user1")
                .store(true)
                .listeners(List.of(listener))
                .build();

        List<ChatMessage> messages = List.of(UserMessage.from("hello"));

        ChatRequestParameters parameters = OpenAiChatRequestParameters.builder()
                .temperature(0.6)
                .topP(0.4)
                .store(false)
                .seed(5)
                .build();

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(messages)
                .parameters(parameters)
                .build();

        OpenAiChatRequestParameters expectedParameters = OpenAiChatRequestParameters.builder()
                .modelName("gpt-4o-mini")
                .temperature(0.6)
                .user("user1")
                .store(false)
                .topP(0.4)
                .seed(5)
                .build();

        // when
        ChatResponse chatResponse = model.chat(chatRequest);

        // then
        assertThat(listener.onRequestCalledTimes).hasValue(1);
        assertThat(listener.onResponseCalledTimes).hasValue(1);
        assertThat(listener.onErrorCalledTimes).hasValue(0);

        assertThat(listener.requestContextReference.get().chatRequest().parameters()).isEqualTo(expectedParameters);
        assertThat(listener.responseContextReference.get().chatResponse()).isEqualTo(chatResponse);
    }

    @Test
    void should_listen_error() {

        // given
        String incorrectApiKey = "banana";

        TestChatModelListener listener = new TestChatModelListener();

        ChatModel model = OpenAiChatModel.builder()
                .apiKey(incorrectApiKey)
                .maxRetries(0)
                .listeners(List.of(listener))
                .build();

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(UserMessage.from("does not matter"))
                .build();

        // when
        Throwable thrownException = null;
        try {
            model.chat(chatRequest);
        } catch (Exception e) {
            thrownException = e;
        }

        // then
        Throwable exceptionReportedToListener = listener.errorContextReference.get().error();
        assertThat(exceptionReportedToListener).isExactlyInstanceOf(AuthenticationException.class);
        assertThat(thrownException).isSameAs(exceptionReportedToListener);
    }
}
