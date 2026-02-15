package com.networknt.genai.model.bedrock.common;

import com.networknt.genai.model.bedrock.BedrockChatModel;
import com.networknt.genai.model.bedrock.BedrockChatRequestParameters;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.common.AbstractChatModelListenerIT;
import com.networknt.genai.model.chat.listener.ChatModelListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static com.networknt.genai.model.bedrock.common.BedrockAiServicesIT.sleepIfNeeded;

@EnabledIfEnvironmentVariable(named = "AWS_SECRET_ACCESS_KEY", matches = ".+")
class BedrockChatModelListenerIT extends AbstractChatModelListenerIT {

    @Override
    protected ChatModel createModel(ChatModelListener listener) {
        return BedrockChatModel.builder()
                .modelId("us.amazon.nova-lite-v1:0")
                .defaultRequestParameters(BedrockChatRequestParameters.builder()
                        .temperature(temperature())
                        .topP(topP())
                        .maxOutputTokens(maxTokens())
                        .build())
                .listeners(List.of(listener))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Override
    protected String modelName() {
        return "us.amazon.nova-lite-v1:0";
    }

    @Override
    protected ChatModel createFailingModel(ChatModelListener listener) {
        return BedrockChatModel.builder()
                .modelId("banana")
                .maxRetries(0)
                .listeners(List.of(listener))
                .build();
    }

    @Override
    protected Class<? extends Exception> expectedExceptionClass() {
        return com.networknt.genai.exception.InvalidRequestException.class;
    }

    @AfterEach
    void afterEach() {
        sleepIfNeeded();
    }
}
