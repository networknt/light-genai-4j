package com.networknt.agent.model.embedding.onnx.bgesmallenv15q;

import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallEnV15QuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallEnV15QuantizedEmbeddingModel();
    }
}
