package com.networknt.agent.model.embedding.onnx.e5smallv2q;

import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.spi.model.embedding.EmbeddingModelFactory;

public class E5SmallV2QuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new E5SmallV2QuantizedEmbeddingModel();
    }
}
