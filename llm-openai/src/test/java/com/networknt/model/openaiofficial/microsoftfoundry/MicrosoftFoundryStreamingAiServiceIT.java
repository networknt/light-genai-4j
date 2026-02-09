package com.networknt.model.openaiofficial.microsoftfoundry;

import com.networknt.model.chat.StreamingChatModel;
import com.networknt.model.chat.response.ChatResponseMetadata;
import com.networknt.model.openaiofficial.OpenAiOfficialChatResponseMetadata;
import com.networknt.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.model.output.TokenUsage;
import com.networknt.service.common.AbstractStreamingAiServiceIT;
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
