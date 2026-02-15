package com.networknt.genai.model.anthropic.common;

import static com.networknt.genai.model.anthropic.common.AnthropicChatModelIT.ANTHROPIC_CHAT_MODEL;

import com.networknt.genai.model.anthropic.AnthropicTokenUsage;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.output.TokenUsage;
import com.networknt.genai.service.common.AbstractAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class AnthropicAiServiceIT extends AbstractAiServiceIT {

    @Override
    protected List<ChatModel> models() {
        return List.of(ANTHROPIC_CHAT_MODEL);
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(ChatModel chatModel) {
        return AnthropicTokenUsage.class;
    }
}
