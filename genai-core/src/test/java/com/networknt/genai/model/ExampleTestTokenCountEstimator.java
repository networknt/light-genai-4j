package com.networknt.genai.model;

import com.networknt.genai.data.message.ChatMessage;

public class ExampleTestTokenCountEstimator implements TokenCountEstimator {

    @Override
    public int estimateTokenCountInText(String text) {
        return text.split(" ").length;
    }

    @Override
    public int estimateTokenCountInMessage(ChatMessage message) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public int estimateTokenCountInMessages(Iterable<ChatMessage> messages) {
        throw new RuntimeException("not implemented");
    }
}
