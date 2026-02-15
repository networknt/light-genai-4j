package com.networknt.genai.model.embedding.onnx.bgesmallen;

import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallEnEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallEnEmbeddingModel();
    }
}
