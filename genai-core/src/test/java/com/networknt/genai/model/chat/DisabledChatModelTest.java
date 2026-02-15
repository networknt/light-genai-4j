package com.networknt.genai.model.chat;

import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.model.DisabledModelTest;
import com.networknt.genai.model.chat.request.ChatRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

class DisabledChatModelTest extends DisabledModelTest<ChatModel> {

    private final ChatModel model = new DisabledChatModel();

    public DisabledChatModelTest() {
        super(ChatModel.class);
    }

    @Test
    void methodsShouldThrowException() {
        UserMessage userMessage = UserMessage.from("Hello");
        performAssertion(() -> model.chat(ChatRequest.builder().messages(userMessage).build()));
        performAssertion(() -> model.chat(userMessage.singleText()));
        performAssertion(() -> model.chat(userMessage));
        performAssertion(() -> model.chat(List.of(userMessage)));
    }
}
