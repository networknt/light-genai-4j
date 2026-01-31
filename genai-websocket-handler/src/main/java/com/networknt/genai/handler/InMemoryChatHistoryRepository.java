package com.networknt.genai.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of ChatHistoryRepository.
 */
public class InMemoryChatHistoryRepository implements ChatHistoryRepository {
    // Stores a list of messages for each session ID
    private final Map<String, List<ChatMessage>> historyMap = new ConcurrentHashMap<>();

    @Override
    public void addMessage(String sessionId, ChatMessage message) {
        historyMap.computeIfAbsent(sessionId, k -> Collections.synchronizedList(new ArrayList<>())).add(message);
    }

    @Override
    public List<ChatMessage> getHistory(String sessionId) {
        List<ChatMessage> history = historyMap.get(sessionId);
        if (history == null) {
            return Collections.emptyList();
        }
        // Return a copy to prevent modification of the underlying list from outside
        synchronized (history) {
            return new ArrayList<>(history);
        }
    }

    @Override
    public void clearHistory(String sessionId) {
        historyMap.remove(sessionId);
    }
}
