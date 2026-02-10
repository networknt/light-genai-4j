package com.networknt.model.ollama.common;

import com.networknt.model.ollama.LC4jOllamaContainer;
import com.networknt.model.ollama.OllamaImage;
import com.networknt.agent.service.common.AbstractAiServiceWithToolsIT;

import static com.networknt.agent.internal.Utils.isNullOrEmpty;
import static com.networknt.model.ollama.AbstractOllamaLanguageModelInfrastructure.OLLAMA_BASE_URL;
import static com.networknt.model.ollama.OllamaImage.LLAMA_3_1;
import static com.networknt.model.ollama.OllamaImage.localOllamaImage;

abstract class AbstractOllamaToolsLanguageModelInfrastructure extends AbstractAiServiceWithToolsIT {

    private static final String MODEL_NAME = LLAMA_3_1;

    static LC4jOllamaContainer ollama;

    static {
        if (isNullOrEmpty(OLLAMA_BASE_URL)) {
            String localOllamaImage = localOllamaImage(MODEL_NAME);
            ollama = new LC4jOllamaContainer(OllamaImage.resolve(OllamaImage.OLLAMA_IMAGE, localOllamaImage))
                    .withModel(MODEL_NAME);
            ollama.start();
            ollama.commitToImage(localOllamaImage);
        }
    }
}
