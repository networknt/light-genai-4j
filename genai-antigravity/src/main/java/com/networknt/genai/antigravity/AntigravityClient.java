package com.networknt.genai.antigravity;

import com.networknt.config.Config;
import com.networknt.genai.ChatMessage;
import com.networknt.genai.GenAiClient;
import com.networknt.genai.RequestOptions;
import com.networknt.genai.StreamCallback;
import com.networknt.status.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntigravityClient implements GenAiClient {
    private static final Logger logger = LoggerFactory.getLogger(AntigravityClient.class);
    private static final AntigravityConfig config = AntigravityConfig.load();

    @Override
    public String chat(List<ChatMessage> messages) {
        return chat(messages, new RequestOptions(config.getModel()));
    }

    @Override
    public String chat(List<ChatMessage> messages, RequestOptions options) {
        return chat(options.getModel() != null ? options.getModel() : config.getModel(), messages);
    }
    
    private static final String CLIENT_METADATA = "{\"ideType\":\"ANTIGRAVITY\",\"platform\":\"PLATFORM_UNSPECIFIED\",\"pluginType\":\"GEMINI\"}";
    private static final String USER_AGENT = "antigravity";
    private static final String X_GOOG_API_CLIENT = "google-cloud-sdk vscode_cloudshelleditor/0.1";
    private static final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

    public String chat(String model, List<ChatMessage> messages) {
        String token = getAccessToken();
        if(token == null) {
            logger.error("Failed to get access token");
            return null;
        }

        try {
            // 1. Fetch Project ID (needed for billing/quota)
            String projectId = fetchProjectId(token);
            
            // 2. Construct Chat Request
            String url = config.getUrl();
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            if (projectId != null) {
                requestBody.put("project", projectId);
            }
            // Add metadata to body as seen in loadCodeAssist, possibly needed for chat too? 
            // openclaw sends it in loadCodeAssist body. 
            // For chat, we stick to Gemini structure but adding 'project' field might be key.
            
            List<Map<String, Object>> contents = new ArrayList<>();
            for (ChatMessage msg : messages) {
                Map<String, Object> contentMap = new HashMap<>();
                String role = msg.getRole();
                if ("assistant".equals(role)) role = "model";
                contentMap.put("role", role);

                List<Map<String, String>> parts = new ArrayList<>();
                Map<String, String> part = new HashMap<>();
                part.put("text", msg.getContent());
                parts.add(part);
                contentMap.put("parts", parts);

                contents.add(contentMap);
            }
            requestBody.put("contents", contents);
            
            String json = Config.getInstance().getMapper().writeValueAsString(requestBody);
            
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .header("User-Agent", USER_AGENT)
                    .header("X-Goog-Api-Client", X_GOOG_API_CLIENT)
                    .header("Client-Metadata", CLIENT_METADATA)
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();
            
            java.net.http.HttpResponse<java.util.stream.Stream<String>> response = 
                client.send(request, java.net.http.HttpResponse.BodyHandlers.ofLines());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                 StringBuilder fullText = new StringBuilder();
                 response.body().forEach(line -> {
                     String chunk = line.trim();
                     if (chunk.startsWith("[")) chunk = chunk.substring(1);
                     if (chunk.endsWith("]")) chunk = chunk.substring(0, chunk.length() - 1);
                     if (chunk.endsWith(",")) chunk = chunk.substring(0, chunk.length() - 1);
                     chunk = chunk.trim();
                     
                     if (!chunk.isEmpty()) {
                         try {
                             Map<String, Object> map = Config.getInstance().getMapper().readValue(chunk, Map.class);
                             List<Map<String, Object>> candidates = (List<Map<String, Object>>) map.get("candidates");
                             if (candidates != null && !candidates.isEmpty()) {
                                 Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                                 if (content != null) {
                                     List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                                     if (parts != null && !parts.isEmpty()) {
                                         String text = (String) parts.get(0).get("text");
                                         if (text != null) fullText.append(text);
                                     }
                                 }
                             }
                         } catch (Exception e) { }
                     }
                 });
                 return fullText.toString();
            } else {
                 String errorBody = response.body().collect(java.util.stream.Collectors.joining("\n"));
                 logger.error("Antigravity API Error: " + response.statusCode() + " " + errorBody);
                 return "Error: " + response.statusCode() + " " + errorBody;
            }

        } catch (Exception e) {
            logger.error("Exception in Antigravity chat", e);
            e.printStackTrace();
        }
        return null;
    }

    private String fetchProjectId(String token) {
        try {
            String loadUrl = "https://cloudcode-pa.googleapis.com/v1internal:loadCodeAssist";
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("ideType", "ANTIGRAVITY");
            metadata.put("platform", "PLATFORM_UNSPECIFIED");
            metadata.put("pluginType", "GEMINI");
            
            Map<String, Object> body = new HashMap<>();
            body.put("metadata", metadata);
            
            String json = Config.getInstance().getMapper().writeValueAsString(body);
            
             java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create(loadUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .header("User-Agent", USER_AGENT)
                    .header("X-Goog-Api-Client", X_GOOG_API_CLIENT)
                    .header("Client-Metadata", CLIENT_METADATA)
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                Map<String, Object> respMap = Config.getInstance().getMapper().readValue(response.body(), Map.class);
                Object projObj = respMap.get("cloudaicompanionProject");
                if (projObj instanceof Map) {
                    return (String) ((Map) projObj).get("id");
                } else if (projObj instanceof String) {
                    return (String) projObj;
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch project ID", e);
        }
        return null;
    }

    @Override
    public void chatStream(List<ChatMessage> messages, StreamCallback callback) {
        chatStream(messages, new RequestOptions(config.getModel()), callback);
    }

    @Override
    public void chatStream(List<ChatMessage> messages, RequestOptions options, StreamCallback callback) {
         // Streaming implementation similar to chat() but processing chunks via callback
         // Use common logic
         callback.onEvent("Antigravity Streaming Response (Stub)"); // TODO: Implement real streaming using same logic
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
