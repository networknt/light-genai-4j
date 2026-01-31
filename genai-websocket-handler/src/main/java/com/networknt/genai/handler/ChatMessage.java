package com.networknt.genai.handler;

/**
 * Represents a single message in the chat conversation.
 */
public class ChatMessage {
    private String role;       // "user", "model", "system"
    private String content;    // The text content
    private long timestamp;    // Epoch timestamp

    public ChatMessage() {
    }

    public ChatMessage(String role, String content, long timestamp) {
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "role='" + role + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
