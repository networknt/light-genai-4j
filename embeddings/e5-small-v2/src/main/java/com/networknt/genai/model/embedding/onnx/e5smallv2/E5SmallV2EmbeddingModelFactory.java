package com.networknt.genai.model.embedding.onnx.e5smallv2;

import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;

public class E5SmallV2EmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new E5SmallV2EmbeddingModel();
    }
}
