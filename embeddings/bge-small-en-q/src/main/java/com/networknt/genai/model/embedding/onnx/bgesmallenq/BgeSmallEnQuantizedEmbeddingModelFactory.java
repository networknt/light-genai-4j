package com.networknt.genai.model.embedding.onnx.bgesmallenq;

import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallEnQuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallEnQuantizedEmbeddingModel();
    }
}
