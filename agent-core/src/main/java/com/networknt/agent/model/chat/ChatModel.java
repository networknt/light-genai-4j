package com.networknt.agent.model.chat;

import static com.networknt.agent.model.ModelProvider.OTHER;
import static com.networknt.agent.model.chat.ChatModelListenerUtils.onError;
import static com.networknt.agent.model.chat.ChatModelListenerUtils.onRequest;
import static com.networknt.agent.model.chat.ChatModelListenerUtils.onResponse;

import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.model.ModelProvider;
import com.networknt.agent.model.chat.listener.ChatModelListener;
import com.networknt.agent.model.chat.request.ChatRequest;
import com.networknt.agent.model.chat.request.ChatRequestParameters;
import com.networknt.agent.model.chat.request.DefaultChatRequestParameters;
import com.networknt.agent.model.chat.response.ChatResponse;
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

    default ChatResponse doChat(ChatRequest chatRequest) {
        throw new RuntimeException("Not implemented");
    }

    default ChatRequestParameters defaultRequestParameters() {
        return DefaultChatRequestParameters.EMPTY;
    }

    default List<ChatModelListener> listeners() {
        return List.of();
    }

    default ModelProvider provider() {
        return OTHER;
    }

    default String chat(String userMessage) {

        ChatRequest chatRequest =
                ChatRequest.builder().messages(UserMessage.from(userMessage)).build();

        ChatResponse chatResponse = chat(chatRequest);

        return chatResponse.aiMessage().text();
    }

    default ChatResponse chat(ChatMessage... messages) {

        ChatRequest chatRequest = ChatRequest.builder().messages(messages).build();

        return chat(chatRequest);
    }

    default ChatResponse chat(List<ChatMessage> messages) {

        ChatRequest chatRequest = ChatRequest.builder().messages(messages).build();

        return chat(chatRequest);
    }

    default Set<Capability> supportedCapabilities() {
        return Set.of();
    }
}
