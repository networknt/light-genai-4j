package com.networknt.genai.model.bedrock;

import com.networknt.genai.Internal;
import com.networknt.genai.data.embedding.Embedding;

@Internal
interface BedrockEmbeddingResponse {

    /**
     * Get embedding
     *
     * @return embedding
     */
    Embedding toEmbedding();

    /**
     * Get input text token count
     *
     * @return input text token count
     */
    int getInputTextTokenCount();
}
