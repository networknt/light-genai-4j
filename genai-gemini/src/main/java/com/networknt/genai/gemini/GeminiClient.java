package com.networknt.genai.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.client.Http2Client;
import com.networknt.config.Config;
import com.networknt.genai.GenAiClient;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.OptionMap;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GeminiClient implements GenAiClient {
    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);
    private static final GeminiConfig config = GeminiConfig.load();
    private static final Http2Client client = Http2Client.getInstance();
    private static final ObjectMapper mapper = Config.getInstance().getMapper();

    @Override
    public String chat(java.util.List<com.networknt.genai.ChatMessage> messages) {
        return chat(config.getModel(), messages);
    }

    public String chat(String model, java.util.List<com.networknt.genai.ChatMessage> messages) {
        String result = null;
        try {
            String endpoint = String.format(config.getUrl(), model) + "?key=" + config.getApiKey();

            // Map ChatMessage list to Gemini structure
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();

            for (com.networknt.genai.ChatMessage msg : messages) {
                Map<String, Object> contentMap = new HashMap<>();
                // Map roles: assistant -> model
                String role = msg.getRole();
                if ("assistant".equals(role)) {
                    role = "model";
                }
                contentMap.put("role", role);

                List<Map<String, String>> parts = new ArrayList<>();
                Map<String, String> part = new HashMap<>();
                part.put("text", msg.getContent());
                parts.add(part);
                contentMap.put("parts", parts);

                contents.add(contentMap);
            }
            requestBody.put("contents", contents);

            String jsonBody = mapper.writeValueAsString(requestBody);
            URI uri = new URI(endpoint);
            ClientConnection connection = client
                    .connect(uri, Http2Client.WORKER, Http2Client.SSL, Http2Client.BUFFER_POOL, OptionMap.EMPTY).get();
            try {
                ClientRequest request = new ClientRequest().setMethod(Methods.POST)
                        .setPath(uri.getPath() + "?" + uri.getQuery());
                request.getRequestHeaders().put(Headers.HOST, uri.getHost());
                request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
                request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");

                final CountDownLatch latch = new CountDownLatch(1);
                final AtomicReference<ClientResponse> reference = new AtomicReference<>();
                connection.sendRequest(request, client.createClientCallback(reference, latch, jsonBody));
                latch.await(10, TimeUnit.SECONDS);

                ClientResponse response = reference.get();
                int statusCode = response.getResponseCode();
                String body = response.getAttachment(Http2Client.RESPONSE_BODY);

                if (statusCode == 200) {
                    Map<String, Object> responseMap = mapper.readValue(body, Map.class);
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        if (content != null) {
                            List<Map<String, Object>> resParts = (List<Map<String, Object>>) content.get("parts");
                            if (resParts != null && !resParts.isEmpty()) {
                                result = (String) resParts.get(0).get("text");
                            }
                        }
                    }
                } else {
                    logger.error("Gemini API error: {} {}", statusCode, body);
                }
            } finally {
                client.returnConnection(connection);
            }
        } catch (Exception e) {
            logger.error("Exception invoking Gemini API", e);
        }
        return result;
    }

    @Override
    public void chatStream(java.util.List<com.networknt.genai.ChatMessage> messages,
            com.networknt.genai.StreamCallback callback) {
        try {
            // URL format for streaming:
            // https://generativelanguage.googleapis.com/v1beta/models/{model}:streamGenerateContent
            String endpoint = String.format(config.getUrl(), config.getModel()).replace(":generateContent",
                    ":streamGenerateContent") + "?key=" + config.getApiKey();

            // Map ChatMessage list to Gemini structure
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();

            for (com.networknt.genai.ChatMessage msg : messages) {
                Map<String, Object> contentMap = new HashMap<>();
                // Map roles: assistant -> model
                String role = msg.getRole();
                if ("assistant".equals(role)) {
                    role = "model";
                }
                contentMap.put("role", role);

                List<Map<String, String>> parts = new ArrayList<>();
                Map<String, String> part = new HashMap<>();
                part.put("text", msg.getContent());
                parts.add(part);
                contentMap.put("parts", parts);

                contents.add(contentMap);
            }
            requestBody.put("contents", contents);

            String jsonBody = mapper.writeValueAsString(requestBody);
            URI uri = new URI(endpoint);
            ClientConnection connection = client
                    .connect(uri, Http2Client.WORKER, Http2Client.SSL, Http2Client.BUFFER_POOL, OptionMap.EMPTY).get();

            ClientRequest request = new ClientRequest().setMethod(Methods.POST)
                    .setPath(uri.getPath() + "?" + uri.getQuery());
            request.getRequestHeaders().put(Headers.HOST, uri.getHost());
            request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
            request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");

            connection.sendRequest(request, new io.undertow.client.ClientCallback<io.undertow.client.ClientExchange>() {
                @Override
                public void completed(io.undertow.client.ClientExchange exchange) {
                    exchange.setResponseListener(
                            new io.undertow.client.ClientCallback<io.undertow.client.ClientExchange>() {
                                @Override
                                public void completed(io.undertow.client.ClientExchange result) {
                                    result.getResponseChannel().getReadSetter()
                                            .set(new org.xnio.ChannelListener<org.xnio.channels.StreamSourceChannel>() {
                                                @Override
                                                public void handleEvent(org.xnio.channels.StreamSourceChannel channel) {
                                                    try {
                                                        java.nio.ByteBuffer buffer = java.nio.ByteBuffer.allocate(1024);
                                                        int read = 0;
                                                        while ((read = channel.read(buffer)) > 0) {
                                                            buffer.flip();
                                                            String chunk = new String(buffer.array(), 0, read,
                                                                    java.nio.charset.StandardCharsets.UTF_8);
                                                            // Gemini returns a JSON array of objects, passed as chunks:
                                                            // [{...}, \r\n {...}]
                                                            // This simple parsing splits by newline and tries to parse
                                                            // objects.
                                                            // Ideally, a streaming JSON parser is needed for
                                                            // robustness.
                                                            // For now, assuming chunks align roughly with objects or
                                                            // filtering valid JSONs.

                                                            // Simple heuristic: Clean up array brackets if present at
                                                            // start/end of stream
                                                            String cleaned = chunk.replace("[", "").replace("]", "")
                                                                    .replace(",", "").trim();
                                                            if (!cleaned.isEmpty()) {
                                                                try {
                                                                    Map<String, Object> responseMap = mapper
                                                                            .readValue(cleaned, Map.class);
                                                                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap
                                                                            .get("candidates");
                                                                    if (candidates != null && !candidates.isEmpty()) {
                                                                        Map<String, Object> content = (Map<String, Object>) candidates
                                                                                .get(0).get("content");
                                                                        if (content != null) {
                                                                            List<Map<String, Object>> resParts = (List<Map<String, Object>>) content
                                                                                    .get("parts");
                                                                            if (resParts != null
                                                                                    && !resParts.isEmpty()) {
                                                                                String text = (String) resParts.get(0)
                                                                                        .get("text");
                                                                                if (text != null)
                                                                                    callback.onEvent(text);
                                                                            }
                                                                        }
                                                                    }
                                                                } catch (Exception e) {
                                                                    // Ignore partial JSONs
                                                                }
                                                            }

                                                            buffer.clear();
                                                        }
                                                        if (read == -1) {
                                                            callback.onComplete();
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
