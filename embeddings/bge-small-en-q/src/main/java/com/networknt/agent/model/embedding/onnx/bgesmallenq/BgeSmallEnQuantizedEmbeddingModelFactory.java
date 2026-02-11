package com.networknt.agent.model.embedding.onnx.bgesmallenq;

import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallEnQuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallEnQuantizedEmbeddingModel();
    }
}
