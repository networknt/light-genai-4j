package com.networknt.genai.model.azure;

import static java.util.Collections.singletonList;

import com.networknt.genai.exception.AuthenticationException;
import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.common.AbstractStreamingChatModelListenerIT;
import com.networknt.genai.model.chat.listener.ChatModelListener;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "AZURE_OPENAI_KEY", matches = ".+")
class AzureOpenAiStreamingChatModelListenerIT extends AbstractStreamingChatModelListenerIT {

    @Override
    protected StreamingChatModel createModel(ChatModelListener listener) {
        return AzureModelBuilders.streamingChatModelBuilder()
                .deploymentName(modelName())
                .temperature(temperature())
                .topP(topP())
                .maxTokens(maxTokens())
                .logRequestsAndResponses(true)
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected String modelName() {
        return AzureModelBuilders.DEFAULT_CHAT_MODEL;
    }

    @Override
    protected StreamingChatModel createFailingModel(ChatModelListener listener) {
        return AzureOpenAiStreamingChatModel.builder()
                .endpoint(System.getenv("AZURE_OPENAI_ENDPOINT"))
                .apiKey("banana")
                .deploymentName(modelName())
                .logRequestsAndResponses(true)
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected Class<? extends Exception> expectedExceptionClass() {
        return AuthenticationException.class;
    }
}
