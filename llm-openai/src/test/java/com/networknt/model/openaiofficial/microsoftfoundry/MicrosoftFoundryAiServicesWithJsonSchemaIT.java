package com.networknt.model.openaiofficial.microsoftfoundry;

import static com.networknt.model.openaiofficial.microsoftfoundry.InternalMicrosoftFoundryTestHelper.AZURE_OPEN_AI_CHAT_MODEL_JSON_WITH_STRICT_SCHEMA;

import com.networknt.agent.model.chat.ChatModel;
import com.networknt.agent.service.common.AbstractAiServiceWithJsonSchemaIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "MICROSOFT_FOUNDRY_API_KEY", matches = ".+")
class MicrosoftFoundryAiServicesWithJsonSchemaIT extends AbstractAiServiceWithJsonSchemaIT {

    @Override
    protected List<ChatModel> models() {
        return InternalMicrosoftFoundryTestHelper.chatModelsWithJsonResponse();
    }

    @Override
    protected boolean supportsRecursion() {
        return true;
    }

    @Override
    protected boolean isStrictJsonSchemaEnabled(ChatModel model) {
        return model == AZURE_OPEN_AI_CHAT_MODEL_JSON_WITH_STRICT_SCHEMA;
    }
}
