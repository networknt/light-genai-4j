package com.networknt.genai.model.chat;

import static com.networknt.genai.model.ModelProvider.OTHER;
import static com.networknt.genai.model.chat.ChatModelListenerUtils.onError;
import static com.networknt.genai.model.chat.ChatModelListenerUtils.onRequest;
import static com.networknt.genai.model.chat.ChatModelListenerUtils.onResponse;

import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.model.ModelProvider;
import com.networknt.genai.model.chat.listener.ChatModelListener;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.request.ChatRequestParameters;
import com.networknt.genai.model.chat.request.DefaultChatRequestParameters;
import com.networknt.genai.model.chat.response.ChatResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a language model that has a chat API.
 *
 * @see StreamingChatModel
 */
public interface ChatModel {

    /**
     * This is the main API to interact with the chat model.
     *
     * @param chatRequest a {@link ChatRequest}, containing all the inputs to the LLM
     * @return a {@link ChatResponse}, containing all the outputs from the LLM
     */
    default ChatResponse chat(ChatRequest chatRequest) {

        ChatRequest finalChatRequest = ChatRequest.builder()
                .messages(chatRequest.messages())
                .parameters(defaultRequestParameters().overrideWith(chatRequest.parameters()))
                .build();

        List<ChatModelListener> listeners = listeners();
        Map<Object, Object> attributes = new ConcurrentHashMap<>();

        onRequest(finalChatRequest, provider(), attributes, listeners);
        try {
            ChatResponse chatResponse = doChat(finalChatRequest);
            onResponse(chatResponse, finalChatRequest, provider(), attributes, listeners);
            return chatResponse;
        } catch (Exception error) {
            onError(error, finalChatRequest, provider(), attributes, listeners);
            throw error;
        }
    }

    /**
     * Submits a chat request to the model.
     *
     * @param chatRequest the chat request
     * @return the chat response
     */
    default ChatResponse doChat(ChatRequest chatRequest) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return the default request parameters
     */
    default ChatRequestParameters defaultRequestParameters() {
        return DefaultChatRequestParameters.EMPTY;
    }

    /**
     * @return the list of listeners
     */
    default List<ChatModelListener> listeners() {
        return List.of();
    }

    /**
     * @return the model provider
     */
    default ModelProvider provider() {
        return OTHER;
    }

    /**
     * Convenience method to chat with the model using a simple string message.
     *
     * @param userMessage the user message
     * @return the model's response text
     */
    default String chat(String userMessage) {

        ChatRequest chatRequest =
                ChatRequest.builder().messages(UserMessage.from(userMessage)).build();

        ChatResponse chatResponse = chat(chatRequest);

        return chatResponse.aiMessage().text();
    }

    /**
     * Convenience method to chat with the model using multiple messages.
     *
     * @param messages the chat messages
     * @return the chat response
     */
    default ChatResponse chat(ChatMessage... messages) {

        ChatRequest chatRequest = ChatRequest.builder().messages(messages).build();

        return chat(chatRequest);
    }

    /**
     * Convenience method to chat with the model using a list of messages.
     *
     * @param messages the chat messages
     * @return the chat response
     */
    default ChatResponse chat(List<ChatMessage> messages) {

        ChatRequest chatRequest = ChatRequest.builder().messages(messages).build();

        return chat(chatRequest);
    }

    /**
     * @return the set of supported capabilities
     */
    default Set<Capability> supportedCapabilities() {
        return Set.of();
    }
}
