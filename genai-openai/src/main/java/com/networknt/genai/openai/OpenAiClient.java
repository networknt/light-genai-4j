package com.networknt.genai.openai;

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

import com.networknt.genai.RequestOptions;

public class OpenAiClient implements GenAiClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAiClient.class);
    private static final OpenAiConfig config = OpenAiConfig.load();
    private static final Http2Client client = Http2Client.getInstance();
    private static final ObjectMapper mapper = Config.getInstance().getMapper();

    @Override
    public String chat(java.util.List<com.networknt.genai.ChatMessage> messages) {
        return chat(messages, new RequestOptions(config.getModel()));
    }

    @Override
    public String chat(java.util.List<com.networknt.genai.ChatMessage> messages, RequestOptions options) {
        return chat(options.getModel() != null ? options.getModel() : config.getModel(), messages);
    }

    public String chat(String model, java.util.List<com.networknt.genai.ChatMessage> messages) {
        String result = null;
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);

            String jsonBody = mapper.writeValueAsString(requestBody);
            URI uri = new URI(config.getUrl());
            ClientConnection connection = client
                    .connect(uri, Http2Client.WORKER, Http2Client.SSL, Http2Client.BUFFER_POOL, OptionMap.EMPTY).get();
            try {
                ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(uri.getPath());
                request.getRequestHeaders().put(Headers.HOST, uri.getHost());
                request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
                request.getRequestHeaders().put(Headers.AUTHORIZATION, "Bearer " + config.getApiKey());
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
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        result = (String) message.get("content");
                    }
                } else {
                    logger.error("OpenAI API error: {} {}", statusCode, body);
                }
            } finally {
                client.returnConnection(connection);
            }
        } catch (Exception e) {
            logger.error("Exception invoking OpenAI API", e);
        }
        return result;
    }

    @Override
    public void chatStream(java.util.List<com.networknt.genai.ChatMessage> messages,
            com.networknt.genai.StreamCallback callback) {
        chatStream(messages, new RequestOptions(config.getModel()), callback);
    }

    @Override
    public void chatStream(java.util.List<com.networknt.genai.ChatMessage> messages,
            RequestOptions options,
            com.networknt.genai.StreamCallback callback) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", options.getModel() != null ? options.getModel() : config.getModel());
            requestBody.put("messages", messages);
            requestBody.put("stream", true);

            String jsonBody = mapper.writeValueAsString(requestBody);
            URI uri = new URI(config.getUrl());
            org.xnio.IoFuture<ClientConnection> future = client.connect(uri, Http2Client.WORKER, Http2Client.SSL,
                    Http2Client.BUFFER_POOL, OptionMap.EMPTY);

            future.addNotifier(new org.xnio.IoFuture.Notifier<ClientConnection, Object>() {
                @Override
                public void notify(org.xnio.IoFuture<? extends ClientConnection> ioFuture, Object attachment) {
                    if (ioFuture.getStatus() == org.xnio.IoFuture.Status.DONE) {
                        try {
                            ClientConnection connection = ioFuture.get();
                            ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(uri.getPath());
                            request.getRequestHeaders().put(Headers.HOST, uri.getHost());
                            request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
                            request.getRequestHeaders().put(Headers.AUTHORIZATION, "Bearer " + config.getApiKey());
                            request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");

                            connection.sendRequest(request,
                                    new io.undertow.client.ClientCallback<io.undertow.client.ClientExchange>() {
                                        @Override
                                        public void completed(io.undertow.client.ClientExchange exchange) {
                                            exchange.setResponseListener(
                                                    new io.undertow.client.ClientCallback<io.undertow.client.ClientExchange>() {
                                                        @Override
                                                        public void completed(
                                                                io.undertow.client.ClientExchange result) {
                                                            result.getResponseChannel().getReadSetter()
                                                                    .set(new org.xnio.ChannelListener<org.xnio.channels.StreamSourceChannel>() {
                                                                        @Override
                                                                        public void handleEvent(
                                                                                org.xnio.channels.StreamSourceChannel channel) {
                                                                            try {
                                                                                java.nio.ByteBuffer buffer = java.nio.ByteBuffer
                                                                                        .allocate(1024);
                                                                                int read = 0;
                                                                                while ((read = channel
                                                                                        .read(buffer)) > 0) {
                                                                                    buffer.flip();
                                                                                    String chunk = new String(
                                                                                            buffer.array(), 0, read,
                                                                                            java.nio.charset.StandardCharsets.UTF_8);
                                                                                    String[] lines = chunk.split("\n");
                                                                                    for (String line : lines) {
                                                                                        line = line.trim();
                                                                                        if (line.startsWith("data: ")) {
                                                                                            String data = line
                                                                                                    .substring(6)
                                                                                                    .trim();
                                                                                            if ("[DONE]".equals(data)) {
                                                                                                callback.onComplete();
                                                                                            } else {
                                                                                                try {
                                                                                                    Map<String, Object> responseMap = mapper
                                                                                                            .readValue(
                                                                                                                    data,
                                                                                                                    Map.class);
                                                                                                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap
                                                                                                            .get("choices");
                                                                                                    if (choices != null
                                                                                                            && !choices
                                                                                                                    .isEmpty()) {
                                                                                                        Map<String, Object> delta = (Map<String, Object>) choices
                                                                                                                .get(0)
                                                                                                                .get("delta");
                                                                                                        if (delta != null
                                                                                                                && delta.containsKey(
                                                                                                                        "content")) {
                                                                                                            String content = (String) delta
                                                                                                                    .get("content");
                                                                                                            if (content != null
                                                                                                                    && !content
                                                                                                                            .isEmpty()) {
                                                                                                                callback.onEvent(
                                                                                                                        content);
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                } catch (Exception e) {
                                                                                                    // Ignore parse
                                                                                                    // error for partial
                                                                                                    // lines
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    buffer.clear();
                                                                                }
                                                                                if (read == -1) {
                                                                                    // End of stream
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

                                            // Write body async
                                            try {
                                                org.xnio.channels.StreamSinkChannel requestChannel = exchange
                                                        .getRequestChannel();
                                                java.nio.ByteBuffer buffer = java.nio.ByteBuffer.wrap(
                                                        jsonBody.getBytes(java.nio.charset.StandardCharsets.UTF_8));

                                                int written = 0;
                                                while (buffer.hasRemaining()) {
                                                    written = requestChannel.write(buffer);
                                                    if (written == 0) {
                                                        requestChannel.getWriteSetter().set(channel -> {
                                                            try {
                                                                while (buffer.hasRemaining()) {
                                                                    int w = channel.write(buffer);
                                                                    if (w == 0)
                                                                        return;
                                                                }
                                                                channel.shutdownWrites();
                                                                channel.flush();
                                                            } catch (java.io.IOException e) {
                                                                logger.error("Error writing body async", e);
                                                                callback.onError(e);
                                                            }
                                                        });
                                                        requestChannel.resumeWrites();
                                                        return;
                                                    }
                                                }
                                                requestChannel.shutdownWrites();
                                                requestChannel.flush();
                                            } catch (Exception e) {
                                                logger.error("Error sending request body", e);
                                                callback.onError(e);
                                            }
                                        }

                                        @Override
                                        public void failed(java.io.IOException e) {
                                            callback.onError(e);
                                        }
                                    });
                        } catch (Exception e) {
                            logger.error("Failed to get connection", e);
                            callback.onError(e);
                        }
                    } else {
                        logger.error("Connection failed", ioFuture.getException());
                        callback.onError(ioFuture.getException());
                    }
                }
            }, null);

        } catch (Exception e) {
            callback.onError(e);
        }
    }
}
