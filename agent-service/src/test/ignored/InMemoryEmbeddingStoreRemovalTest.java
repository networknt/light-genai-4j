package com.networknt.agent.store.embedding.inmemory;

import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import com.networknt.agent.store.embedding.EmbeddingStore;
import com.networknt.agent.store.embedding.EmbeddingStoreWithRemovalIT;

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
