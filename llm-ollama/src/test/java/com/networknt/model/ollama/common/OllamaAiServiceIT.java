package com.networknt.model.ollama.common;

import com.networknt.agent.model.chat.ChatModel;
import com.networknt.model.ollama.OllamaChatModel;
import com.networknt.model.openaiofficial.OpenAiOfficialChatModel;
import com.networknt.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.agent.model.output.TokenUsage;
import com.networknt.agent.service.common.AbstractAiServiceIT;

import java.util.List;

import static com.networknt.model.ollama.common.OllamaChatModelIT.OLLAMA_CHAT_MODEL_WITH_TOOLS;
import static com.networknt.model.ollama.common.OllamaChatModelIT.OPEN_AI_CHAT_MODEL_WITH_TOOLS;

class OllamaAiServiceIT extends AbstractAiServiceIT {

    @Override
    protected List<ChatModel> models() {
        return List.of(OLLAMA_CHAT_MODEL_WITH_TOOLS, OPEN_AI_CHAT_MODEL_WITH_TOOLS);
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(ChatModel chatModel) {
        if (chatModel instanceof OpenAiOfficialChatModel) {
            return OpenAiOfficialTokenUsage.class;
        } else if (chatModel instanceof OllamaChatModel) {
            return TokenUsage.class;
        } else {
            throw new IllegalStateException("Unknown model type: " + chatModel.getClass());
        }
    }
}
