package com.networknt.genai.store.embedding.inmemory;

import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import com.networknt.genai.store.embedding.EmbeddingStore;
import com.networknt.genai.store.embedding.EmbeddingStoreWithRemovalIT;

class InMemoryEmbeddingStoreRemovalTest extends EmbeddingStoreWithRemovalIT {

    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

    EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

    @Override
    protected EmbeddingStore<TextSegment> embeddingStore() {
        return embeddingStore;
    }

    @Override
    protected EmbeddingModel embeddingModel() {
        return embeddingModel;
    }
}
