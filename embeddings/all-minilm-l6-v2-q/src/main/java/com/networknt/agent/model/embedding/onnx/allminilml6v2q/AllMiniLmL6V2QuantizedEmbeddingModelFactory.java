package com.networknt.agent.model.embedding.onnx.allminilml6v2q;

import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.spi.model.embedding.EmbeddingModelFactory;

public class AllMiniLmL6V2QuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new AllMiniLmL6V2QuantizedEmbeddingModel();
    }
}
