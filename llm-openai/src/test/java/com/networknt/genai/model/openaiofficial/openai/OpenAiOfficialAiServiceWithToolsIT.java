package com.networknt.genai.model.openaiofficial.openai;

import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.service.common.AbstractAiServiceWithToolsIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiOfficialAiServiceWithToolsIT extends AbstractAiServiceWithToolsIT {

    @Override
    protected List<ChatModel> models() {
        return InternalOpenAiOfficialTestHelper.chatModelsNormalAndStrictTools();
    }

    @Override
    protected boolean supportsMapParameters() {
        // When strictTools=true , Map parameters are not supported as there is no JsonSchemaElement for them at the
        // moment.
        return false;
    }

    @Override
    protected boolean supportsRecursion() {
        return true;
    }

    @Override
    protected boolean verifyModelInteractions() {
        return true;
    }
}
