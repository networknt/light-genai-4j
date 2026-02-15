package com.networknt.genai.model.openaiofficial.microsoftfoundry;

import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.response.ChatResponseMetadata;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialChatResponseMetadata;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.genai.model.output.TokenUsage;
import com.networknt.genai.service.common.AbstractStreamingAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "MICROSOFT_FOUNDRY_API_KEY", matches = ".+")
class MicrosoftFoundryStreamingAiServiceIT extends AbstractStreamingAiServiceIT {

    @Override
    protected List<StreamingChatModel> models() {
        return InternalMicrosoftFoundryTestHelper.chatModelsStreamingNormalAndJsonStrict();
    }

    @Override
    protected Class<? extends ChatResponseMetadata> chatResponseMetadataType(StreamingChatModel streamingChatModel) {
        return OpenAiOfficialChatResponseMetadata.class;
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(StreamingChatModel streamingChatModel) {
        return OpenAiOfficialTokenUsage.class;
    }
}
