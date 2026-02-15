package com.networknt.genai.model.embedding.onnx.bgesmallzhv15q;

import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallZhV15QuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallZhV15QuantizedEmbeddingModel();
    }
}
