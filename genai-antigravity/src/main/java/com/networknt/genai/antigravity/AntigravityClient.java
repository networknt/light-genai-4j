package com.networknt.genai.antigravity;

import com.networknt.client.Http2Client;
import com.networknt.config.Config;
import com.networknt.genai.ChatMessage;
import com.networknt.genai.GenAiClient;
import com.networknt.genai.RequestOptions;
import com.networknt.genai.StreamCallback;
import com.networknt.status.Status;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.OptionMap;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class AntigravityClient implements GenAiClient {
    private static final Logger logger = LoggerFactory.getLogger(AntigravityClient.class);
    private static final AntigravityConfig config = AntigravityConfig.load();
    private static final Http2Client client = Http2Client.getInstance();

    @Override
    public String chat(List<ChatMessage> messages) {
        return chat(messages, new RequestOptions(config.getModel()));
    }

    @Override
    public String chat(List<ChatMessage> messages, RequestOptions options) {
        return chat(options.getModel() != null ? options.getModel() : config.getModel(), messages);
    }
    
    public String chat(String model, List<ChatMessage> messages) {
        // Implementation for synchronous chat
        // Fetch OAuth token first
        String token = getAccessToken();
        if(token == null) {
            logger.error("Failed to get access token");
            System.err.println("Failed to get access token");
            return null;
        }

        String url = config.getUrl();
        ClientConnection connection = null;
        try {
            URI uri = new URI(url);
            connection = client.connect(new URI(url), Http2Client.WORKER, Http2Client.SSL, Http2Client.BUFFER_POOL, OptionMap.EMPTY).get();
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);
            
            ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(uri.getPath());
            request.getRequestHeaders().put(Headers.HOST, "localhost");
            request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
            request.getRequestHeaders().put(Headers.AUTHORIZATION, "Bearer " + token);
            request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, "chunked");

            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicReference<ClientResponse> reference = new AtomicReference<>();
            
            String json = Config.getInstance().getMapper().writeValueAsString(requestBody);
            
            connection.sendRequest(request, client.createClientCallback(reference, latch, json));
            
            if (latch.await(30, TimeUnit.SECONDS)) {
                ClientResponse response = reference.get();
                String responseBody = response.getAttachment(Http2Client.RESPONSE_BODY);
                if (response.getResponseCode() >= 200 && response.getResponseCode() < 300) {
                     return responseBody;
                } else {
                     logger.error("Antigravity API Error: " + response.getResponseCode() + " " + responseBody);
                     return "Error: " + response.getResponseCode() + " " + responseBody;
                }
            } else {
                logger.error("Timeout connecting to Antigravity API");
                System.err.println("Timeout connecting to Antigravity API");
            }

        } catch (Exception e) {
            logger.error("Exception in Antigravity chat", e);
            e.printStackTrace();
        } finally {
            if(connection != null) try { connection.close(); } catch(Exception e) { e.printStackTrace(); }
        }
        return null;
    }

    @Override
    public void chatStream(List<ChatMessage> messages, StreamCallback callback) {
        chatStream(messages, new RequestOptions(config.getModel()), callback);
    }

    @Override
    public void chatStream(List<ChatMessage> messages, RequestOptions options, StreamCallback callback) {
         // Streaming implementation similar to other clients but with OAuth header
         // For now, minimal stub to verify compilation
         callback.onEvent("Antigravity Streaming Response (Stub)");
         callback.onComplete();
    }
    
    private final AntigravityAuth auth = new AntigravityAuth();

    // Helper to get OAuth token
    private String getAccessToken() {
        String token = auth.getAccessToken();
        if (token == null) {
            System.err.println("Failed to retrieve access token from AntigravityAuth.");
        }
        return token;
    }
}
