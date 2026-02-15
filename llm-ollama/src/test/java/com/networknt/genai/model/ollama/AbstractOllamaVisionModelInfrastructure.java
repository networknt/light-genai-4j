package com.networknt.genai.model.ollama;

import static com.networknt.genai.internal.Utils.isNullOrEmpty;
import static com.networknt.genai.model.ollama.AbstractOllamaLanguageModelInfrastructure.OLLAMA_BASE_URL;
import static com.networknt.genai.model.ollama.OllamaImage.LLAMA_3_2_VISION;
import static com.networknt.genai.model.ollama.OllamaImage.localOllamaImage;

class AbstractOllamaVisionModelInfrastructure {

    protected static final String MODEL_NAME = LLAMA_3_2_VISION;

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
