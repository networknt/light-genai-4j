package com.networknt.genai.ollama;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.client.Http2Client;
import com.networknt.config.Config;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.client.ClientExchange;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.OptionMap;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import com.networknt.genai.GenAiClient;
import com.networknt.genai.ChatMessage;

public class OllamaClient implements GenAiClient {
    private static final Logger logger = LoggerFactory.getLogger(OllamaClient.class);
    private static final OllamaConfig config = OllamaConfig.load();
    private static final Http2Client client = Http2Client.getInstance();
    private static final ObjectMapper mapper = Config.getInstance().getMapper();

    @Override
    public String chat(java.util.List<ChatMessage> messages) {
        return chat(config.getModel(), messages);
    }

    public String chat(String model, java.util.List<ChatMessage> messages) {
        String result = null;
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            requestBody.put("stream", false);

            String jsonBody = mapper.writeValueAsString(requestBody);
            URI uri = new URI(config.getOllamaUrl());
            ClientConnection connection = client
                    .connect(uri, Http2Client.WORKER, Http2Client.SSL, Http2Client.BUFFER_POOL, OptionMap.EMPTY).get();
            try {
                ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath("/api/chat");
                request.getRequestHeaders().put(Headers.HOST, uri.getHost());
                request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
                request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");

                final CountDownLatch latch = new CountDownLatch(1);
                final AtomicReference<ClientResponse> reference = new AtomicReference<>();
                connection.sendRequest(request, client.createClientCallback(reference, latch, jsonBody));
                latch.await(5, TimeUnit.SECONDS); // Timeout for response

                int statusCode = reference.get().getResponseCode();
                String body = reference.get().getAttachment(Http2Client.RESPONSE_BODY);
                if (statusCode == 200) {
                    Map<String, Object> responseMap = mapper.readValue(body, Map.class);
                    Map<String, Object> message = (Map<String, Object>) responseMap.get("message");
                    if (message != null) {
                        result = (String) message.get("content");
                    }
                } else {
                    logger.error("Ollama API error: {} {}", statusCode, body);
                }
            } finally {
                client.returnConnection(connection);
            }
        } catch (Exception e) {
            logger.error("Exception invoking Ollama API", e);
        }
        return result;
    }

    @Override
    public void chatStream(java.util.List<ChatMessage> messages, com.networknt.genai.StreamCallback callback) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getModel());
            requestBody.put("messages", messages);
            requestBody.put("stream", true);

            String jsonBody = mapper.writeValueAsString(requestBody);
            URI uri = new URI(config.getOllamaUrl());
            ClientConnection connection = client
                    .connect(uri, Http2Client.WORKER, Http2Client.SSL, Http2Client.BUFFER_POOL, OptionMap.EMPTY).get();

            ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath("/api/chat");
            request.getRequestHeaders().put(Headers.HOST, uri.getHost());
            request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
            request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");

            connection.sendRequest(request, new io.undertow.client.ClientCallback<ClientExchange>() {
                @Override
                public void completed(io.undertow.client.ClientExchange exchange) {
                    exchange.setResponseListener(
                            new io.undertow.client.ClientCallback<io.undertow.client.ClientExchange>() {
                                @Override
                                public void completed(io.undertow.client.ClientExchange result) {
                                    result.getResponseChannel().getReadSetter().set(
                                            new org.xnio.ChannelListener<org.xnio.channels.StreamSourceChannel>() {
                                                @Override
                                                public void handleEvent(org.xnio.channels.StreamSourceChannel channel) {
                                                    try {
                                                        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(1024);
                                                        int read = 0;
                                                        while ((read = channel.read(buffer)) > 0) {
                                                            buffer.flip();
                                                            String chunk = new String(buffer.array(), 0, read,
                                                                    java.nio.charset.StandardCharsets.UTF_8);
                                                            // Process NDJSON chunk
                                                            // Each line is a JSON object
                                                            String[] lines = chunk.split("\n");
                                                            for (String line : lines) {
                                                                if (line.trim().isEmpty())
                                                                    continue;
                                                                try {
                                                                    Map<String, Object> responseMap = mapper
                                                                            .readValue(line, Map.class);
                                                                    Map<String, Object> message = (Map<String, Object>) responseMap
                                                                            .get("message");
                                                                    if (message != null) {
                                                                        String content = (String) message
                                                                                .get("content");
                                                                        if (content != null)
                                                                            callback.onEvent(content);
                                                                    }
                                                                    Boolean done = (Boolean) responseMap.get("done");
                                                                    if (Boolean.TRUE.equals(done)) {
                                                                        callback.onComplete();
                                                                    }
                                                                } catch (Exception e) {
                                                                    // Partial JSON handling or parse error -
                                                                    // specialized handling might be needed for split
                                                                    // JSONs across chunks
                                                                    // For MVP, logging and continuing
                                                                }
                                                            }
                                                            buffer.clear();
                                                        }
                                                        if (read == -1) {
                                                            // Channel closed
                                                        }
                                                    } catch (java.io.IOException e) {
                                                        callback.onError(e);
                                                    }
                                                }
                                            });
                                    result.getResponseChannel().resumeReads();
                                }

                                @Override
                                public void failed(java.io.IOException e) {
                                    callback.onError(e);
                                }
                            });
                }

                @Override
                public void failed(java.io.IOException e) {
                    callback.onError(e);
                }
            });
        } catch (Exception e) {
            callback.onError(e);
        }
    }
}
