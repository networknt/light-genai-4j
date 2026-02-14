package com.networknt.agent.model.embedding.onnx.bgesmallzhv15;

import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallZhV15EmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallZhV15EmbeddingModel();
    }
}
