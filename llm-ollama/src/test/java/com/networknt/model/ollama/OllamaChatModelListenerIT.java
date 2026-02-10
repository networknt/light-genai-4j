package com.networknt.model.ollama;

import static com.networknt.agent.internal.Utils.isNullOrEmpty;
import static com.networknt.model.ollama.AbstractOllamaLanguageModelInfrastructure.OLLAMA_BASE_URL;
import static com.networknt.model.ollama.AbstractOllamaLanguageModelInfrastructure.ollamaBaseUrl;
import static com.networknt.model.ollama.OllamaImage.LLAMA_3_1;
import static com.networknt.model.ollama.OllamaImage.localOllamaImage;
import static java.util.Collections.singletonList;

import com.networknt.agent.exception.ModelNotFoundException;
import com.networknt.agent.model.chat.ChatModel;
import com.networknt.agent.model.chat.common.AbstractChatModelListenerIT;
import com.networknt.agent.model.chat.listener.ChatModelListener;

class OllamaChatModelListenerIT extends AbstractChatModelListenerIT {

    private static final String MODEL_NAME = LLAMA_3_1;
    private static LC4jOllamaContainer ollama;

    static {
        if (isNullOrEmpty(OLLAMA_BASE_URL)) {
            String localOllamaImage = localOllamaImage(MODEL_NAME);
            ollama = new LC4jOllamaContainer(OllamaImage.resolve(OllamaImage.OLLAMA_IMAGE, localOllamaImage))
                    .withModel(MODEL_NAME);
            ollama.start();
            ollama.commitToImage(localOllamaImage);
        }
    }

    @Override
    protected ChatModel createModel(ChatModelListener listener) {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl(ollama))
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
        return MODEL_NAME;
    }

    @Override
    protected ChatModel createFailingModel(ChatModelListener listener) {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl(ollama))
                .modelName("banana")
                .maxRetries(0)
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
    protected boolean assertResponseId() {
        return false;
    }
}
