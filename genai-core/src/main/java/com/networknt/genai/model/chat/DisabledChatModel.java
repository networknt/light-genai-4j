package com.networknt.genai.model.chat;

import com.networknt.genai.model.ModelDisabledException;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;

/**
 * A {@link ChatModel} which throws a {@link ModelDisabledException} for all of its methods
 * <p>
 * This could be used in tests, or in libraries that extend this one to conditionally enable or disable functionality.
 * </p>
 */
public class DisabledChatModel implements ChatModel {

    /**
     * Creates a new instance.
     */
    public DisabledChatModel() {
    }

    @Override
    public ChatResponse doChat(final ChatRequest chatRequest) {
        throw new ModelDisabledException("ChatModel is disabled");
    }
}
