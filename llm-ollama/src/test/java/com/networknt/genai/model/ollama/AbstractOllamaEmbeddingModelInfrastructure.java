package com.networknt.genai.model.ollama;

import static com.networknt.genai.internal.Utils.isNullOrEmpty;
import static com.networknt.genai.model.ollama.AbstractOllamaLanguageModelInfrastructure.OLLAMA_BASE_URL;
import static com.networknt.genai.model.ollama.OllamaImage.ALL_MINILM_MODEL;
import static com.networknt.genai.model.ollama.OllamaImage.OLLAMA_IMAGE;
import static com.networknt.genai.model.ollama.OllamaImage.localOllamaImage;

class AbstractOllamaEmbeddingModelInfrastructure {

    private static final String MODEL_NAME = ALL_MINILM_MODEL;

    static LC4jOllamaContainer ollama;

    static {
        if (isNullOrEmpty(OLLAMA_BASE_URL)) {
            String localOllamaImage = localOllamaImage(MODEL_NAME);
            ollama = new LC4jOllamaContainer(OllamaImage.resolve(OLLAMA_IMAGE, localOllamaImage))
                    .withModel(MODEL_NAME);
            ollama.start();
            ollama.commitToImage(localOllamaImage);
        }
    }
}
