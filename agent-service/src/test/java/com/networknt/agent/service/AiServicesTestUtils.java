package com.networknt.agent.service;

import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.model.chat.ChatModel;
import com.networknt.agent.model.chat.request.ChatRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AiServicesTestUtils {

    public static ChatRequest chatRequest(String userMessage) {
        return ChatRequest.builder()
                .messages(UserMessage.from(userMessage))
                .build();
    }

    public static void verifyNoMoreInteractionsFor(ChatModel model) {
        try {
            verify(model, atLeastOnce()).doChat(any());
        } catch (Throwable ignored) {
            // don't care if it was called or not
        }
        try {
            verify(model, atLeastOnce()).defaultRequestParameters();
        } catch (Throwable ignored) {
            // don't care if it was called or not
        }
        try {
            verify(model, atLeastOnce()).supportedCapabilities();
        } catch (Throwable ignored) {
            // don't care if it was called or not
        }
        try {
            verify(model, atLeastOnce()).listeners();
        } catch (Throwable ignored) {
            // don't care if it was called or not
        }
        try {
            verify(model, atLeastOnce()).provider();
        } catch (Throwable ignored) {
            // don't care if it was called or not
        }
        verifyNoMoreInteractions(model);
    }
}
