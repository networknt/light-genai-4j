package com.networknt.genai.model.azure.common;

import static com.networknt.genai.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

import com.networknt.genai.model.azure.AzureModelBuilders;
import com.networknt.genai.model.azure.AzureOpenAiChatModel;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.service.common.AbstractAiServiceWithJsonSchemaIT;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "AZURE_OPENAI_KEY", matches = ".+")
class AzureOpenAiAiServiceWithJsonSchemaIT extends AbstractAiServiceWithJsonSchemaIT {

    AzureOpenAiChatModel model = AzureModelBuilders.chatModelBuilder()
            .supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA))
            .strictJsonSchema(false)
            .temperature(0.0)
            .build();

    AzureOpenAiChatModel modelWithStrictJsonSchema = AzureModelBuilders.chatModelBuilder()
            .supportedCapabilities(Set.of(RESPONSE_FORMAT_JSON_SCHEMA))
            .strictJsonSchema(true)
            .temperature(0.0)
            .build();

    @Override
    protected List<ChatModel> models() {
        return List.of(model, modelWithStrictJsonSchema);
    }

    @Override
    protected boolean supportsRecursion() {
        return true;
    }

    @Override
    protected boolean isStrictJsonSchemaEnabled(ChatModel model) {
        return model == modelWithStrictJsonSchema;
    }

    @AfterEach
    void afterEach() throws InterruptedException {
        String ciDelaySeconds = System.getenv("CI_DELAY_SECONDS_AZURE_OPENAI");
        if (ciDelaySeconds != null) {
            Thread.sleep(Integer.parseInt(ciDelaySeconds) * 1000L);
        }
    }
}
