package com.networknt.genai.model.embedding.onnx.bgesmallenv15;

import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallEnV15EmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallEnV15EmbeddingModel();
    }
}
