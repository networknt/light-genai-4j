package com.networknt.genai.model.anthropic.common;

import static com.networknt.genai.model.anthropic.common.AnthropicStreamingChatModelIT.ANTHROPIC_STREAMING_CHAT_MODEL;

import com.networknt.genai.model.anthropic.AnthropicChatResponseMetadata;
import com.networknt.genai.model.anthropic.AnthropicTokenUsage;
import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.response.ChatResponseMetadata;
import com.networknt.genai.model.output.TokenUsage;
import com.networknt.genai.service.common.AbstractStreamingAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class AnthropicStreamingAiServiceIT extends AbstractStreamingAiServiceIT {

    @Override
    protected List<StreamingChatModel> models() {
        return List.of(ANTHROPIC_STREAMING_CHAT_MODEL);
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(StreamingChatModel model) {
        return AnthropicTokenUsage.class;
    }

    @Override
    protected Class<? extends ChatResponseMetadata> chatResponseMetadataType(StreamingChatModel model) {
        return AnthropicChatResponseMetadata.class;
    }
}
