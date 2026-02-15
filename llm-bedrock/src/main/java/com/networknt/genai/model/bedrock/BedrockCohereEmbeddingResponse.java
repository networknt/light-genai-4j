package com.networknt.genai.model.bedrock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.networknt.genai.Internal;

/**
 * See more details <a href="https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-embed.html">here</a>.
 */
@Internal
class BedrockCohereEmbeddingResponse {

    static class Embeddings {

        @JsonProperty("float")
        private float[][] floatEmbeddings;

        public float[][] getFloatEmbeddings() {
            return floatEmbeddings;
        }

        public void setFloatEmbeddings(float[][] floatEmbeddings) {
            this.floatEmbeddings = floatEmbeddings;
        }

    }

    private Embeddings embeddings;
    private int inputTextTokenCount;

    public Embeddings getEmbeddings() {
        return embeddings;
    }

    public void setEmbeddings(Embeddings embeddings) {
        this.embeddings = embeddings;
    }

    public int getInputTextTokenCount() {
        return inputTextTokenCount;
    }

    public void setInputTextTokenCount(int inputTextTokenCount) {
        this.inputTextTokenCount = inputTextTokenCount;
    }
}
