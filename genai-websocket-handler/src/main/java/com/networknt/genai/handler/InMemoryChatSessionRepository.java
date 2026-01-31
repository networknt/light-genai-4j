package com.networknt.genai.handler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of ChatSessionRepository.
 */
public class InMemoryChatSessionRepository implements ChatSessionRepository {
    private final Map<String, ChatSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public ChatSession createSession(String userId, String model) {
        String sessionId = UUID.randomUUID().toString();
        ChatSession session = new ChatSession(sessionId, userId, model, null);
        sessionMap.put(sessionId, session);
        return session;
    }

    @Override
    public ChatSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }

    @Override
    public void deleteSession(String sessionId) {
        sessionMap.remove(sessionId);
    }
}
