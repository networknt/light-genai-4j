package com.networknt.genai.model.azure.common;

import static com.networknt.genai.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

import com.networknt.genai.model.azure.AzureModelBuilders;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.service.common.AbstractAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "AZURE_OPENAI_KEY", matches = ".+")
class AzureOpenAiAiServiceIT extends AbstractAiServiceIT {

    @Override
    protected List<ChatModel> models() {
        return List.of(
                AzureModelBuilders.chatModelBuilder()
                        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
                        .strictJsonSchema(true)
                        .build(),
                AzureModelBuilders.chatModelBuilder()
                        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
                        .strictJsonSchema(false)
                        .build());
    }
}
