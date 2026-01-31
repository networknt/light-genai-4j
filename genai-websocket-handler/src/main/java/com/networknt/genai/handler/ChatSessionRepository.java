package com.networknt.genai.handler;

/**
 * Interface for storing and retrieving chat session data.
 */
public interface ChatSessionRepository {
    /**
     * Create a new chat session.
     * @param userId The user ID
     * @param model The model name
     * @return The created session
     */
    ChatSession createSession(String userId, String model);

    /**
     * Retrieve a session by ID.
     * @param sessionId The session ID
     * @return The session, or null if not found
     */
    ChatSession getSession(String sessionId);

    /**
     * Delete a session.
     * @param sessionId The session ID
     */
    void deleteSession(String sessionId);
}
