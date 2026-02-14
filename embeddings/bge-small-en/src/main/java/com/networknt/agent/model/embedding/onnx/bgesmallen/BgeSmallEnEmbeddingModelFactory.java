package com.networknt.agent.model.embedding.onnx.bgesmallen;

import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallEnEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallEnEmbeddingModel();
    }
}
