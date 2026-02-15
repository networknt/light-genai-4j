package com.networknt.genai.model.openaiofficial.microsoftfoundry;

import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.genai.model.output.TokenUsage;
import com.networknt.genai.service.common.AbstractAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "MICROSOFT_FOUNDRY_API_KEY", matches = ".+")
class MicrosoftFoundryAiServiceIT extends AbstractAiServiceIT {

    @Override
    protected List<ChatModel> models() {
        return InternalMicrosoftFoundryTestHelper.chatModelsNormalAndJsonStrict();
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(ChatModel chatModel) {
        return OpenAiOfficialTokenUsage.class;
    }
}
