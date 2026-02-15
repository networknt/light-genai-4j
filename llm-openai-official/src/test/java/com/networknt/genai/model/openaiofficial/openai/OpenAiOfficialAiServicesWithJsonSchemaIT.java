package com.networknt.genai.model.openaiofficial.openai;

import static com.networknt.genai.model.openaiofficial.openai.InternalOpenAiOfficialTestHelper.OPEN_AI_CHAT_MODEL_JSON_WITH_STRICT_SCHEMA;

import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.service.common.AbstractAiServiceWithJsonSchemaIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiOfficialAiServicesWithJsonSchemaIT extends AbstractAiServiceWithJsonSchemaIT {

    @Override
    protected List<ChatModel> models() {
        return InternalOpenAiOfficialTestHelper.chatModelsWithJsonResponse();
    }

    @Override
    protected boolean supportsRecursion() {
        return true;
    }

    @Override
    protected boolean isStrictJsonSchemaEnabled(ChatModel model) {
        return model == OPEN_AI_CHAT_MODEL_JSON_WITH_STRICT_SCHEMA;
    }
}
