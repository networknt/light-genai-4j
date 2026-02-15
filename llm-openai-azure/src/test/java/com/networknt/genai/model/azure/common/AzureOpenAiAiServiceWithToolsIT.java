package com.networknt.genai.model.azure.common;

import static java.util.Collections.singletonList;

import com.networknt.genai.model.azure.AzureModelBuilders;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.service.common.AbstractAiServiceWithToolsIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "AZURE_OPENAI_KEY", matches = ".+")
class AzureOpenAiAiServiceWithToolsIT extends AbstractAiServiceWithToolsIT {

    @Override
    protected List<ChatModel> models() {
        return singletonList(
                AzureModelBuilders.chatModelBuilder().temperature(0.0).build());
    }
}
