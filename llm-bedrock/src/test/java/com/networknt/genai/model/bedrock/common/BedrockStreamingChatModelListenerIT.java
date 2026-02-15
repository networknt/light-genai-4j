package com.networknt.genai.model.bedrock.common;

import static com.networknt.genai.model.bedrock.common.BedrockAiServicesIT.sleepIfNeeded;
import static java.util.Collections.singletonList;

import com.networknt.genai.model.bedrock.BedrockStreamingChatModel;
import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.common.AbstractStreamingChatModelListenerIT;
import com.networknt.genai.model.chat.listener.ChatModelListener;
import com.networknt.genai.model.chat.request.DefaultChatRequestParameters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "AWS_SECRET_ACCESS_KEY", matches = ".+")
class BedrockStreamingChatModelListenerIT extends AbstractStreamingChatModelListenerIT {

    @Override
    protected StreamingChatModel createModel(ChatModelListener listener) {
        return BedrockStreamingChatModel.builder()
                .modelId(modelName())
                .defaultRequestParameters(DefaultChatRequestParameters.builder()
                        .modelName(modelName())
                        .temperature(temperature())
                        .topP(topP())
                        .maxOutputTokens(maxTokens())
                        .build())
                .logRequests(true)
                .logResponses(true)
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected String modelName() {
        return "us.amazon.nova-lite-v1:0";
    }

    @Override
    protected StreamingChatModel createFailingModel(ChatModelListener listener) {
        return BedrockStreamingChatModel.builder()
                .modelId("banana")
                .logRequests(true)
                .logResponses(true)
                .listeners(singletonList(listener))
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
