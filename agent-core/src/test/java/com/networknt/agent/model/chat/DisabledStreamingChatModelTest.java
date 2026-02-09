package com.networknt.agent.model.chat;

import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.model.DisabledModelTest;
import com.networknt.agent.model.chat.request.ChatRequest;
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
