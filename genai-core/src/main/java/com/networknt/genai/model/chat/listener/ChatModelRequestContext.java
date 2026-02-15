package com.networknt.genai.model.chat.listener;

import com.networknt.genai.model.ModelProvider;
import com.networknt.genai.model.chat.request.ChatRequest;

import java.util.Map;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * The chat model request context.
 * It contains the {@link ChatRequest}, {@link ModelProvider} and attributes.
 * The attributes can be used to pass data between methods of a {@link ChatModelListener}
 * or between multiple {@link ChatModelListener}s.
 */
public class ChatModelRequestContext {

    private final ChatRequest chatRequest;
    private final ModelProvider modelProvider;
    private final Map<Object, Object> attributes;

    /**
     * Creates a new request context.
     *
     * @param chatRequest   the chat request
     * @param modelProvider the model provider
     * @param attributes    the attributes
     */
    public ChatModelRequestContext(ChatRequest chatRequest,
                                   ModelProvider modelProvider,
                                   Map<Object, Object> attributes) {
        this.chatRequest = ensureNotNull(chatRequest, "chatRequest");
        this.modelProvider = modelProvider;
        this.attributes = ensureNotNull(attributes, "attributes");
    }

    /**
     * @return The chat request.
     */
    public ChatRequest chatRequest() {
        return chatRequest;
    }

    /**
     * @return The model provider.
     */
    public ModelProvider modelProvider() {
        return modelProvider;
    }

    /**
     * @return The attributes map. It can be used to pass data between methods of a {@link ChatModelListener}
     * or between multiple {@link ChatModelListener}s.
     */
    public Map<Object, Object> attributes() {
        return attributes;
    }
}
