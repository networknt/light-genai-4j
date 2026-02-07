package com.networknt.genai;

import java.util.List;

import java.util.List;

public interface GenAiClient {
    /**
     * Generates a text completion for the given list of chat messages.
     * 
     * @param messages The list of chat messages (history).
     * @return The generated text response from the model.
     */
    String chat(List<ChatMessage> messages);

    String chat(List<ChatMessage> messages, RequestOptions options);

    /**
     * Generates a text completion stream for the given list of chat messages.
     * 
     * @param messages The list of chat messages (history).
     * @param callback The callback to receive chunks, completion, and errors.
     */
    void chatStream(List<ChatMessage> messages, StreamCallback callback);

    void chatStream(List<ChatMessage> messages, RequestOptions options, StreamCallback callback);
}
