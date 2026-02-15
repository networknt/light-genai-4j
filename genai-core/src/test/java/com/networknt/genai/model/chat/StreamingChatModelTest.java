package com.networknt.genai.model.chat;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.chat.response.StreamingChatResponseHandler;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class StreamingChatModelTest implements WithAssertions {

    public static class StreamingUpperCaseEchoModel implements StreamingChatModel {

        @Override
        public void doChat(ChatRequest chatRequest, StreamingChatResponseHandler handler) {
            List<ChatMessage> messages = chatRequest.messages();
            UserMessage lastMessage = (UserMessage) messages.get(messages.size() - 1);
            ChatResponse chatResponse = ChatResponse.builder()
                    .aiMessage(new AiMessage(lastMessage.singleText().toUpperCase(Locale.ROOT)))
                    .build();
            handler.onCompleteResponse(chatResponse);
        }
    }

    public static final class CollectorResponseHandler implements StreamingChatResponseHandler {

        private final List<ChatResponse> responses = new ArrayList<>();

        public List<ChatResponse> responses() {
            return responses;
        }

        @Override
        public void onPartialResponse(String partialResponse) {
        }

        @Override
        public void onCompleteResponse(ChatResponse completeResponse) {
            responses.add(completeResponse);
        }

        @Override
        public void onError(Throwable error) {
        }
    }

    @Test
    void generate() {
        StreamingChatModel model = new StreamingUpperCaseEchoModel();

        {
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new UserMessage("Hello"));
            messages.add(new AiMessage("Hi"));
            messages.add(new UserMessage("How are you?"));

            CollectorResponseHandler handler = new CollectorResponseHandler();
            model.chat(messages, handler);

            ChatResponse response = handler.responses().get(0);

            assertThat(response.aiMessage().text()).isEqualTo("HOW ARE YOU?");
            assertThat(response.tokenUsage()).isNull();
            assertThat(response.finishReason()).isNull();
        }

        {
            CollectorResponseHandler handler = new CollectorResponseHandler();
            model.chat("How are you?", handler);

            ChatResponse response = handler.responses().get(0);

            assertThat(response.aiMessage().text()).isEqualTo("HOW ARE YOU?");
            assertThat(response.tokenUsage()).isNull();
            assertThat(response.finishReason()).isNull();
        }
    }
}
