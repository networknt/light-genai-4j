package com.networknt.genai.model.embedding.onnx.allminilml6v2;

import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;

public class AllMiniLmL6V2EmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new AllMiniLmL6V2EmbeddingModel();
    }
}
