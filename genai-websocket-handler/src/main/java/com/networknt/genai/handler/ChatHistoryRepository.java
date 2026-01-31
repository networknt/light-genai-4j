package com.networknt.genai.handler;

import java.util.List;

/**
 * Interface for storing and retrieving chat message history.
 */
public interface ChatHistoryRepository {
    /**
     * Add a message to the history for a session.
     * @param sessionId The session ID
     * @param message The message to add
     */
    void addMessage(String sessionId, ChatMessage message);

    /**
     * Get the message history for a session.
     * @param sessionId The session ID
     * @return List of messages
     */
    List<ChatMessage> getHistory(String sessionId);

    /**
     * Clear all history for a session.
     * @param sessionId The session ID
     */
    void clearHistory(String sessionId);
}
