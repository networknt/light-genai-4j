package com.networknt.genai.model.anthropic;

import static com.networknt.genai.internal.Utils.isNullOrEmpty;
import static com.networknt.genai.model.anthropic.internal.mapper.AnthropicMapper.toAnthropicMessages;
import static com.networknt.genai.model.anthropic.internal.mapper.AnthropicMapper.toAnthropicSystemPrompt;
import static com.networknt.genai.model.anthropic.internal.mapper.AnthropicMapper.toAnthropicToolChoice;
import static com.networknt.genai.model.anthropic.internal.mapper.AnthropicMapper.toAnthropicTools;
import static com.networknt.genai.model.chat.request.ResponseFormatType.JSON;
import static com.networknt.genai.model.chat.request.ResponseFormatType.TEXT;

import com.networknt.genai.Internal;
import com.networknt.genai.exception.UnsupportedFeatureException;
import com.networknt.genai.model.anthropic.internal.api.AnthropicCacheType;
import com.networknt.genai.model.anthropic.internal.api.AnthropicCreateMessageRequest;
import com.networknt.genai.model.anthropic.internal.api.AnthropicFormat;
import com.networknt.genai.model.anthropic.internal.api.AnthropicMetadata;
import com.networknt.genai.model.anthropic.internal.api.AnthropicOutputConfig;
import com.networknt.genai.model.anthropic.internal.api.AnthropicThinking;
import com.networknt.genai.model.anthropic.internal.api.AnthropicTool;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.request.ChatRequestParameters;
import com.networknt.genai.model.chat.request.ResponseFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Internal
class InternalAnthropicHelper {

    private InternalAnthropicHelper() {}

    static void validate(ChatRequestParameters parameters) {
        List<String> unsupportedFeatures = new ArrayList<>();
        if (parameters.frequencyPenalty() != null) {
            unsupportedFeatures.add("Frequency Penalty");
        }
        if (parameters.presencePenalty() != null) {
            unsupportedFeatures.add("Presence Penalty");
        }
        if (parameters.responseFormat() != null && parameters.responseFormat().type() == JSON
                && parameters.responseFormat().jsonSchema() == null) {
            unsupportedFeatures.add("Schemaless JSON response format");
        }

        if (!unsupportedFeatures.isEmpty()) {
            if (unsupportedFeatures.size() == 1) {
                throw new UnsupportedFeatureException(unsupportedFeatures.get(0) + " is not supported by Anthropic");
            }
            throw new UnsupportedFeatureException(
                    String.join(", ", unsupportedFeatures) + " are not supported by Anthropic");
        }
    }

    static AnthropicCreateMessageRequest createAnthropicRequest(
            ChatRequest chatRequest,
            AnthropicThinking thinking,
            boolean sendThinking,
            AnthropicCacheType cacheType,
            AnthropicCacheType toolsCacheType,
            boolean stream,
            String toolChoiceName,
            Boolean disableParallelToolUse,
            List<AnthropicServerTool> serverTools,
            Set<String> toolMetadataKeysToSend,
            String userId,
            Map<String, Object> customParameters,
            Boolean strictTools) {

        AnthropicCreateMessageRequest.Builder requestBuilder = AnthropicCreateMessageRequest.builder().stream(stream)
                .model(chatRequest.modelName())
                .messages(toAnthropicMessages(chatRequest.messages(), sendThinking))
                .system(toAnthropicSystemPrompt(chatRequest.messages(), cacheType))
                .maxTokens(chatRequest.maxOutputTokens())
                .stopSequences(chatRequest.stopSequences())
                .temperature(chatRequest.temperature())
                .topP(chatRequest.topP())
                .topK(chatRequest.topK())
                .thinking(thinking)
                .outputConfig(toAnthropicOutputConfig(chatRequest.responseFormat()))
                .customParameters(customParameters);

        List<AnthropicTool> tools = new ArrayList<>();
        if (!isNullOrEmpty(serverTools)) {
            tools.addAll(toAnthropicTools(serverTools));
        }
        if (!isNullOrEmpty(chatRequest.toolSpecifications())) {
            tools.addAll(toAnthropicTools(chatRequest.toolSpecifications(), toolsCacheType, toolMetadataKeysToSend, strictTools));
        }
        if (!tools.isEmpty()) {
            requestBuilder.tools(tools);
        }

        if (chatRequest.toolChoice() != null) {
            requestBuilder.toolChoice(
                    toAnthropicToolChoice(chatRequest.toolChoice(), toolChoiceName, disableParallelToolUse));
        }

        if (!isNullOrEmpty(userId)) {
            requestBuilder.metadata(AnthropicMetadata.builder().userId(userId).build());
        }

        return requestBuilder.build();
    }

    public static AnthropicOutputConfig toAnthropicOutputConfig(ResponseFormat responseFormat) {
        if (responseFormat == null || responseFormat.type() == TEXT || responseFormat.jsonSchema() == null) {
            return null;
        }

        return AnthropicOutputConfig.builder()
                .format(AnthropicFormat.fromJsonSchema(responseFormat.jsonSchema()))
                .build();
    }
}
