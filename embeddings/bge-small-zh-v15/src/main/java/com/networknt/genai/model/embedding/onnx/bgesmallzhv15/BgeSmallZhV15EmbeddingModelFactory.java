package com.networknt.genai.model.embedding.onnx.bgesmallzhv15;

import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.spi.model.embedding.EmbeddingModelFactory;

public class BgeSmallZhV15EmbeddingModelFactory implements EmbeddingModelFactory {

    @Override
    public EmbeddingModel create() {
        return new BgeSmallZhV15EmbeddingModel();
    }
}
