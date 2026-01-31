package com.networknt.genai.bedrock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import com.networknt.genai.GenAiClient;

public class BedrockClient implements GenAiClient {
    private static final Logger logger = LoggerFactory.getLogger(BedrockClient.class);
    private static final BedrockConfig config = BedrockConfig.load();
    private final BedrockRuntimeAsyncClient client;

    public BedrockClient() {
        this.client = BedrockRuntimeAsyncClient.builder()
                .region(Region.of(config.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Override
    public String chat(java.util.List<com.networknt.genai.ChatMessage> messages) {
        // Defaulting to Claude 3 format since it's the most common Chat model on
        // Bedrock
        // Format: { "anthropic_version": "bedrock-2023-05-31", "messages": [...] }
        // For other models, we might need a Strategy/Factory pattern based on modelId
        try {
            java.util.Map<String, Object> bodyMap = new java.util.HashMap<>();
            bodyMap.put("anthropic_version", "bedrock-2023-05-31");
            bodyMap.put("max_tokens", 1000);
            bodyMap.put("messages", messages);

            String jsonBody = com.networknt.config.Config.getInstance().getMapper().writeValueAsString(bodyMap);
            return invokeModel(config.getModelId(), jsonBody);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Error creating Bedrock request body", e);
            return null;
        }
    }

    @Override
    public void chatStream(java.util.List<com.networknt.genai.ChatMessage> messages,
            com.networknt.genai.StreamCallback callback) {
        try {
            java.util.Map<String, Object> bodyMap = new java.util.HashMap<>();
            bodyMap.put("anthropic_version", "bedrock-2023-05-31");
            bodyMap.put("max_tokens", 1000);
            bodyMap.put("messages", messages);

            String jsonBody = com.networknt.config.Config.getInstance().getMapper().writeValueAsString(bodyMap);

            SdkBytes payload = SdkBytes.fromString(jsonBody, StandardCharsets.UTF_8);
            software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamRequest request = software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamRequest
                    .builder()
                    .modelId(config.getModelId())
                    .body(payload)
                    .contentType("application/json")
                    .build();

            client.invokeModelWithResponseStream(request,
                    software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamResponseHandler
                            .builder()
                            .onEventStream(publisher -> publisher.subscribe(event -> {
                                if (event instanceof software.amazon.awssdk.services.bedrockruntime.model.PayloadPart) {
                                    software.amazon.awssdk.services.bedrockruntime.model.PayloadPart part = (software.amazon.awssdk.services.bedrockruntime.model.PayloadPart) event;
                                    String chunk = part.bytes().asUtf8String();
                                    // Parse Bedrock partial JSON
                                    try {
                                        com.fasterxml.jackson.databind.JsonNode node = com.networknt.config.Config
                                                .getInstance().getMapper().readTree(chunk);
                                        if (node.has("delta") && node.get("delta").has("text")) {
                                            callback.onEvent(node.get("delta").get("text").asText());
                                        }
                                    } catch (Exception e) {
                                        // log or ignore
                                    }
                                }
                            }))
                            .onComplete(() -> callback.onComplete())
                            .onError(e -> callback.onError(e))
                            .build())
                    .whenComplete((response, error) -> {
                        if (error != null) {
                            callback.onError(error);
                        }
                    });

        } catch (Exception e) {
            callback.onError(e);
        }
    }

    public String invokeModel(String body) {
        return invokeModel(config.getModelId(), body);
    }

    public String invokeModel(String modelId, String body) {
        try {
            SdkBytes payload = SdkBytes.fromString(body, StandardCharsets.UTF_8);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .body(payload)
                    .contentType("application/json")
                    .build();

            InvokeModelResponse response = client.invokeModel(request).join();
            return response.body().asUtf8String();

        } catch (Exception e) {
            logger.error("Exception invoking Bedrock API", e);
            throw new RuntimeException(e);
        }
    }
}
