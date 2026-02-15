package com.networknt.genai.model.azure.common;

import static com.networknt.genai.model.azure.common.AzureOpenAiStreamingChatModelIT.AZURE_OPEN_AI_STREAMING_CHAT_MODEL;

import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.service.common.AbstractStreamingAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "AZURE_OPENAI_KEY", matches = ".+")
class AzureOpenAiStreamingAiServiceIT extends AbstractStreamingAiServiceIT {

    @Override
    protected List<StreamingChatModel> models() {
        return List.of(
                AZURE_OPEN_AI_STREAMING_CHAT_MODEL
                // TODO add more model configs
                );
    }
}
