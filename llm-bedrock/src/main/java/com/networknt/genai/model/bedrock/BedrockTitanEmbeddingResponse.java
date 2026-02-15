package com.networknt.genai.model.bedrock;

import com.networknt.genai.Internal;
import com.networknt.genai.data.embedding.Embedding;

@Internal
class BedrockTitanEmbeddingResponse implements BedrockEmbeddingResponse {

    private float[] embedding;
    private int inputTextTokenCount;

    @Override
    public Embedding toEmbedding() {
        return new Embedding(embedding);
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(final float[] embedding) {
        this.embedding = embedding;
    }

    public int getInputTextTokenCount() {
        return inputTextTokenCount;
    }

    public void setInputTextTokenCount(final int inputTextTokenCount) {
        this.inputTextTokenCount = inputTextTokenCount;
    }
}
