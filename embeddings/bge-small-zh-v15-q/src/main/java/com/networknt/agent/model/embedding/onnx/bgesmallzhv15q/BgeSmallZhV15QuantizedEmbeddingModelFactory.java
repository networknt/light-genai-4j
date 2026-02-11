package com.networknt.agent.model.embedding.onnx.bgesmallzhv15q;

import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallZhV15QuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallZhV15QuantizedEmbeddingModel();
    }
}
