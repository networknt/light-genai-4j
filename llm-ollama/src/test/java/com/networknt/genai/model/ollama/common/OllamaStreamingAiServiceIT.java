package com.networknt.genai.model.ollama.common;

import static com.networknt.genai.model.ollama.common.OllamaStreamingChatModelIT.OLLAMA_CHAT_MODEL_WITH_TOOLS;
import static com.networknt.genai.model.ollama.common.OllamaStreamingChatModelIT.OPEN_AI_CHAT_MODEL_WITH_TOOLS;

import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.response.ChatResponseMetadata;
import com.networknt.genai.model.ollama.OllamaStreamingChatModel;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialChatResponseMetadata;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialStreamingChatModel;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.genai.model.output.TokenUsage;
import com.networknt.genai.service.common.AbstractStreamingAiServiceIT;
import java.util.List;

class OllamaStreamingAiServiceIT extends AbstractStreamingAiServiceIT {

    @Override
    protected List<StreamingChatModel> models() {
        return List.of(OLLAMA_CHAT_MODEL_WITH_TOOLS, OPEN_AI_CHAT_MODEL_WITH_TOOLS);
    }

    @Override
    protected Class<? extends ChatResponseMetadata> chatResponseMetadataType(StreamingChatModel streamingChatModel) {
        if (streamingChatModel instanceof OpenAiOfficialStreamingChatModel) {
            return OpenAiOfficialChatResponseMetadata.class;
        } else if (streamingChatModel instanceof OllamaStreamingChatModel) {
            return ChatResponseMetadata.class;
        } else {
            throw new IllegalStateException("Unknown model type: " + streamingChatModel.getClass());
        }
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(StreamingChatModel streamingChatModel) {
        if (streamingChatModel instanceof OpenAiOfficialStreamingChatModel) {
            return OpenAiOfficialTokenUsage.class;
        } else if (streamingChatModel instanceof OllamaStreamingChatModel) {
            return TokenUsage.class;
        } else {
            throw new IllegalStateException("Unknown model type: " + streamingChatModel.getClass());
        }
    }
}
