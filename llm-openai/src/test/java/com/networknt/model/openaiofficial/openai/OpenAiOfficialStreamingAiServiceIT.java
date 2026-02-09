package com.networknt.model.openaiofficial.openai;

import com.networknt.model.chat.StreamingChatModel;
import com.networknt.model.chat.response.ChatResponseMetadata;
import com.networknt.model.openaiofficial.OpenAiOfficialChatResponseMetadata;
import com.networknt.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.model.output.TokenUsage;
import com.networknt.service.common.AbstractStreamingAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiOfficialStreamingAiServiceIT extends AbstractStreamingAiServiceIT {

    @Override
    protected List<StreamingChatModel> models() {
        return InternalOpenAiOfficialTestHelper.chatModelsStreamingNormalAndJsonStrict();
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
