package com.networknt.genai.model.embedding.onnx.bgesmallenv15q;

import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallEnV15QuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallEnV15QuantizedEmbeddingModel();
    }
}
