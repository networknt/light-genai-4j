package com.networknt.genai.model.openaiofficial.openai;

import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.genai.model.output.TokenUsage;
import com.networknt.genai.service.common.AbstractAiServiceIT;
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
