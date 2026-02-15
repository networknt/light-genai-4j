package com.networknt.genai.model.bedrock.common;

import static com.networknt.genai.model.bedrock.TestedModels.CLAUDE_3_HAIKU;
import static com.networknt.genai.model.bedrock.common.BedrockAiServicesIT.sleepIfNeeded;

import com.networknt.genai.model.bedrock.BedrockChatModel;
import com.networknt.genai.model.bedrock.BedrockChatResponseMetadata;
import com.networknt.genai.model.bedrock.BedrockTokenUsage;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.common.AbstractChatModelIT;
import com.networknt.genai.model.chat.request.ChatRequestParameters;
import com.networknt.genai.model.chat.response.ChatResponseMetadata;
import com.networknt.genai.model.output.TokenUsage;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "AWS_SECRET_ACCESS_KEY", matches = ".+")
class BedrockChatModelWithVisionIT extends AbstractChatModelIT {

    @Override
    protected List<ChatModel> models() {
        return List.of(CLAUDE_3_HAIKU); // , LLAMA_3_2_90B); NOT AVAILABLE FOR ME AT THIS MOMENT
    }

    @Override
    protected String customModelName() {
        return "cohere.command-r-v1:0";
    }

    @Override
    protected ChatRequestParameters createIntegrationSpecificParameters(int maxOutputTokens) {
        return ChatRequestParameters.builder().maxOutputTokens(maxOutputTokens).build();
    }

    @Override
    protected ChatModel createModelWith(ChatRequestParameters parameters) {
        return BedrockChatModel.builder()
                .defaultRequestParameters(parameters)
                // force a working model with stopSequence parameter for @Tests
                .modelId("cohere.command-r-v1:0")
                .build();
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(ChatModel model) {
        return BedrockTokenUsage.class;
    }

    @Override
    protected boolean supportsJsonResponseFormat() {
        return false; // output format not supported
    }

    @Override
    protected boolean supportsJsonResponseFormatWithSchema() {
        return false; // output format not supported
    }

    @Override
    protected boolean supportsJsonResponseFormatWithRawSchema() {
        return false; // output format not supported
    }

    @Override
    protected Class<? extends ChatResponseMetadata> chatResponseMetadataType(final ChatModel model) {
        return BedrockChatResponseMetadata.class;
    }

    @AfterEach
    void afterEach() {
        sleepIfNeeded();
    }
}
