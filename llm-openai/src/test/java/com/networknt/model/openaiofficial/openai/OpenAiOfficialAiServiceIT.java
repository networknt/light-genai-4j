package com.networknt.model.openaiofficial.openai;

import com.networknt.model.chat.ChatModel;
import com.networknt.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.model.output.TokenUsage;
import com.networknt.service.common.AbstractAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiOfficialAiServiceIT extends AbstractAiServiceIT {

    @Override
    protected List<ChatModel> models() {
        return InternalOpenAiOfficialTestHelper.chatModelsNormalAndJsonStrict();
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(ChatModel chatModel) {
        return OpenAiOfficialTokenUsage.class;
    }
}
