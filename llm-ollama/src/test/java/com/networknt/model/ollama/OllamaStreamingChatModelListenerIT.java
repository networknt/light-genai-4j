package com.networknt.model.ollama;

import static com.networknt.model.ollama.AbstractOllamaLanguageModelInfrastructure.ollama;
import static com.networknt.model.ollama.OllamaImage.TINY_DOLPHIN_MODEL;
import static java.util.Collections.singletonList;

import com.networknt.agent.exception.ModelNotFoundException;
import com.networknt.agent.model.chat.StreamingChatModel;
import com.networknt.agent.model.chat.common.AbstractStreamingChatModelListenerIT;
import com.networknt.agent.model.chat.listener.ChatModelListener;

public class OllamaStreamingChatModelListenerIT extends AbstractStreamingChatModelListenerIT {

    @Override
    protected StreamingChatModel createModel(ChatModelListener listener) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(AbstractOllamaLanguageModelInfrastructure.ollamaBaseUrl(ollama))
                .modelName(modelName())
                .temperature(temperature())
                .topP(topP())
                .numPredict(maxTokens())
                .logRequests(true)
                .logResponses(true)
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected String modelName() {
        return TINY_DOLPHIN_MODEL;
    }

    @Override
    protected StreamingChatModel createFailingModel(ChatModelListener listener) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(AbstractOllamaLanguageModelInfrastructure.ollamaBaseUrl(ollama))
                .modelName("banana")
                .logRequests(true)
                .logResponses(true)
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected Class<? extends Exception> expectedExceptionClass() {
        return ModelNotFoundException.class;
    }

    @Override
    protected boolean supportsTools() {
        return false;
    }

    @Override
    protected boolean assertResponseId() {
        return false;
    }
}
