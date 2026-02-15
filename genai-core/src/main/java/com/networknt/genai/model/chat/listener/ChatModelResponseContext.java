package com.networknt.genai.model.chat.listener;

import com.networknt.genai.model.ModelProvider;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;

import java.util.Map;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * The chat response context.
 * It contains {@link ChatResponse}, corresponding {@link ChatRequest}, {@link ModelProvider} and attributes.
 * The attributes can be used to pass data between methods of a {@link ChatModelListener}
 * or between multiple {@link ChatModelListener}s.
 */
public class ChatModelResponseContext {

    private final ChatResponse chatResponse;
    private final ChatRequest chatRequest;
    private final ModelProvider modelProvider;
    private final Map<Object, Object> attributes;

    /**
     * Creates a new response context.
     *
     * @param chatResponse  the chat response
     * @param chatRequest   the chat request
     * @param modelProvider the model provider
     * @param attributes    the attributes
     */
    public ChatModelResponseContext(ChatResponse chatResponse,
                                    ChatRequest chatRequest,
                                    ModelProvider modelProvider,
                                    Map<Object, Object> attributes) {
        this.chatResponse = ensureNotNull(chatResponse, "chatResponse");
        this.chatRequest = ensureNotNull(chatRequest, "chatRequest");
        this.modelProvider = modelProvider;
        this.attributes = ensureNotNull(attributes, "attributes");
    }

    /**
     * @return The chat response.
     */
    public ChatResponse chatResponse() {
        return chatResponse;
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
