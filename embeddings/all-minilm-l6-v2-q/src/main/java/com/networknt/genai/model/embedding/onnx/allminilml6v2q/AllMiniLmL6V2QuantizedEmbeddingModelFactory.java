package com.networknt.genai.model.embedding.onnx.allminilml6v2q;

import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;

public class AllMiniLmL6V2QuantizedEmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new AllMiniLmL6V2QuantizedEmbeddingModel();
    }
}
