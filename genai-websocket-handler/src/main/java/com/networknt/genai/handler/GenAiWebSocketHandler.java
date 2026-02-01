package com.networknt.genai.handler;

import com.networknt.genai.ChatMessage;
import com.networknt.genai.GenAiClient;
import com.networknt.genai.StreamCallback;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.websocket.handler.WebSocketApplicationHandler;
import io.undertow.websockets.core.*;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class GenAiWebSocketHandler implements WebSocketApplicationHandler {
    private static final Logger logger = LoggerFactory.getLogger(GenAiWebSocketHandler.class);

    private final ChatSessionRepository sessionRepository;
    private final ChatHistoryRepository historyRepository;
    private final GenAiClient genAiClient;

    public GenAiWebSocketHandler() {
        // Load dependencies via SingletonServiceFactory (which uses ServiceLoader or service.yml)
        // If not found, fallback to default in-memory implementations for repos
        ChatSessionRepository sessionRepo = SingletonServiceFactory.getBean(ChatSessionRepository.class);
        this.sessionRepository = sessionRepo != null ? sessionRepo : new InMemoryChatSessionRepository();

        ChatHistoryRepository historyRepo = SingletonServiceFactory.getBean(ChatHistoryRepository.class);
        this.historyRepository = historyRepo != null ? historyRepo : new InMemoryChatHistoryRepository();

        this.genAiClient = SingletonServiceFactory.getBean(GenAiClient.class);
        if (this.genAiClient == null) {
            logger.warn("No GenAiClient implementation found. Chat functionality will not work.");
        }
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        // Parse query parameters for userId.
        // WebSocketHttpExchange does not expose query parameters directly in a map, 
        // we parse the query string.
        String queryString = exchange.getQueryString();
        String tempUserId = "anonymous";
        String tempModel = "default";
        
        if (queryString != null && !queryString.isEmpty()) {
            // Simple parsing for userId=...
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    if ("userId".equals(kv[0])) tempUserId = kv[1];
                    if ("model".equals(kv[0])) tempModel = kv[1];
                }
            }
        }
        final String userId = tempUserId;
        final String model = tempModel;
        
        logger.info("New connection for user: {}", userId);
        
        // Create or get session
        ChatSession session = sessionRepository.createSession(userId, model);
        String sessionId = session.getSessionId();

        channel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                final String payload = message.getData();
                logger.debug("Received message from {}: {}", userId, payload);

                // Dispatch to worker thread to avoid blocking IO thread with GenAiClient calls
                channel.getWorker().execute(() -> {
                    // Add user message to history
                    ChatMessage userMsg = new ChatMessage("user", payload);
                    historyRepository.addMessage(sessionId, userMsg);

                    // Invoke LLM
                    List<ChatMessage> history = historyRepository.getHistory(sessionId);

                    if (genAiClient != null) {
                        StringBuilder responseBuilder = new StringBuilder();

                        genAiClient.chatStream(history, new StreamCallback() {
                            private final StringBuilder buffer = new StringBuilder();

                            @Override
                            public void onEvent(String content) {
                                // Buffer content and send only when newline is detected to avoid single word per line
                                try {
                                    buffer.append(content);
                                    responseBuilder.append(content);
                                    if (buffer.toString().contains("\n")) {
                                        WebSockets.sendText(buffer.toString(), channel, null);
                                        buffer.setLength(0);
                                    }
                                } catch (Exception e) {
                                    logger.error("Error sending message chunk", e);
                                }
                            }

                            @Override
                            public void onComplete() {
                                // Send remaining buffer
                                if (!buffer.isEmpty()) {
                                    try {
                                        WebSockets.sendText(buffer.toString(), channel, null);
                                    } catch (Exception e) {
                                        logger.error("Error sending last message chunk", e);
                                    }
                                }
                                // Add full model response to history
                                ChatMessage modelMsg = new ChatMessage("assistant", responseBuilder.toString());
                                historyRepository.addMessage(sessionId, modelMsg);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                logger.error("GenAI Client Error", throwable);
                                WebSockets.sendText("Error: " + throwable.getMessage(), channel, null);
                            }
                        });
                    } else {
                        WebSockets.sendText("System Error: No GenAiClient configured.", channel, null);
                    }
                });
            }
        });
        channel.resumeReceives();
    }
}
