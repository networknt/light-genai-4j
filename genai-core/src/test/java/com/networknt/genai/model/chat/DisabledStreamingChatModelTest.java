package com.networknt.genai.model.chat;

import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.model.DisabledModelTest;
import com.networknt.genai.model.chat.request.ChatRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

class DisabledStreamingChatModelTest extends DisabledModelTest<StreamingChatModel> {

    private final StreamingChatModel model = new DisabledStreamingChatModel();

    public DisabledStreamingChatModelTest() {
        super(StreamingChatModel.class);
    }

    @Test
    void methodsShouldThrowException() {
        UserMessage userMessage = UserMessage.from("Hello");
        performAssertion(() -> model.chat(ChatRequest.builder().messages(userMessage).build(), null));
        performAssertion(() -> model.chat(userMessage.singleText(), null));
        performAssertion(() -> model.chat(List.of(userMessage), null));
    }
}
