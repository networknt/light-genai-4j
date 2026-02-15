package com.networknt.genai.model.bedrock.common;

import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.service.common.AbstractAiServiceWithToolsIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static com.networknt.genai.model.bedrock.TestedModels.MISTRAL_LARGE;
import static com.networknt.genai.model.bedrock.common.BedrockAiServicesIT.sleepIfNeeded;

@EnabledIfEnvironmentVariable(named = "AWS_SECRET_ACCESS_KEY", matches = ".+")
class BedrockAiServiceWithToolsIT extends AbstractAiServiceWithToolsIT {

    @Override
    protected List<ChatModel> models() {
        return List.of(MISTRAL_LARGE);
    }

    @Override
    @Disabled("Bedrock is too strict and expects assistant message after tool message")
    protected void should_keep_memory_consistent_using_return_immediate(ChatModel model) {}

    @Override
    @Disabled("Mistral is hallucinating in this test")
    protected void should_return_immediately_from_first_tool_when_not_called_in_parallel(ChatModel model) {}

    @AfterEach
    void afterEach() {
        sleepIfNeeded();
    }
}
