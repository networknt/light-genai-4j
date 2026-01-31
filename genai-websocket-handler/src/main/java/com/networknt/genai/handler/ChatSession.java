package com.networknt.genai.handler;

import java.util.Map;

/**
 * Represents the state of a conversation session.
 */
public class ChatSession {
    private String sessionId;
    private String userId;
    private String model;      // The generic model name (e.g., "gemini-pro")
    private Map<String, Object> parameters; // Model parameters (temperature, etc.)

    public ChatSession() {
    }

    public ChatSession(String sessionId, String userId, String model, Map<String, Object> parameters) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.model = model;
        this.parameters = parameters;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "ChatSession{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", model='" + model + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
