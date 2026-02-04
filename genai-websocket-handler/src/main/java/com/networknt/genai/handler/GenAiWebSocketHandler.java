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

    private final AgentRepository agentRepository;
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
        
        AgentRepository agentRepo = SingletonServiceFactory.getBean(AgentRepository.class);
        this.agentRepository = agentRepo != null ? agentRepo : new ConfigAgentRepository();

        this.genAiClient = SingletonServiceFactory.getBean(GenAiClient.class);
        if (this.genAiClient == null) {
            logger.warn("No GenAiClient implementation found. Chat functionality will not work.");
        }
    }

    @Override
    public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
        // Parse query parameters for userId and agentId
        String queryString = exchange.getQueryString();
        String tempUserId = "anonymous";
        String tempAgentId = "default";
        String tempSessionId = null;
        
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    if ("userId".equals(kv[0])) tempUserId = kv[1];
                    if ("agentId".equals(kv[0])) tempAgentId = kv[1];
                    if ("sessionId".equals(kv[0])) tempSessionId = kv[1];
                }
            }
        }
        
        final String userId = tempUserId;
        final String agentId = tempAgentId;
        
        // Resolve Agent
        final AgentDefinition agentDef = agentRepository.getAgent(agentId);
        if (agentDef == null) {
            logger.error("Agent not found: {}", agentId);
            // Consider sending error and closing, but for now just log
        }
        
        // Ensure Session ID
        final String sessionId = tempSessionId != null ? tempSessionId : java.util.UUID.randomUUID().toString();
        
        logger.info("New connection - Agent: {}, User: {}, Session: {}", agentId, userId, sessionId);
        
        // Create or get session (persisted)
        ChatSession session = sessionRepository.createSession(userId, agentId); // Assuming createSession can handle this or mapping logic
        
        // Setup Receive Listener
        channel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                final String payload = message.getData();
                logger.debug("Received message from {}: {}", userId, payload);

                channel.getWorker().execute(() -> {
                    // Add user message to history
                    ChatMessage userMsg = new ChatMessage("user", payload);
                    historyRepository.addMessage(sessionId, userMsg);

                    // Invoke LLM
                    List<ChatMessage> history = historyRepository.getHistory(sessionId);
                    
                    // Inject System Prompt if it's the start of history or handled by Client/Repo logic
                    // For simplicity, we assume the Client or Repo handles ensuring system prompt is present
                    // OR we prepend it here if history is empty. 
                    // Better approach: Let GenAiClient or Model logic handle system prompt placement via RequestOptions
                    
                    if (genAiClient != null) {
                        StringBuilder responseBuilder = new StringBuilder();
                        
                        // Create Request Options
                        com.networknt.genai.RequestOptions options = new com.networknt.genai.RequestOptions();
                        if (agentDef != null) {
                             if (agentDef.getModel() != null) options.setModel(agentDef.getModel());
                             if (agentDef.getSystemPrompt() != null) options.setSystemPrompt(agentDef.getSystemPrompt());
                        }

                        genAiClient.chatStream(history, options, new StreamCallback() {
                            private final StringBuilder buffer = new StringBuilder();

                            @Override
                            public void onEvent(String content) {
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
                                if (buffer.length() > 0) {
                                    try {
                                        WebSockets.sendText(buffer.toString(), channel, null);
                                    } catch (Exception e) {
                                        logger.error("Error sending last message chunk", e);
                                    }
                                }
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

