package com.networknt.agent.store.embedding.inmemory;

import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import com.networknt.agent.store.embedding.EmbeddingStore;
import com.networknt.agent.store.embedding.EmbeddingStoreWithFilteringIT;

/**
 * Tests if {@link InMemoryEmbeddingStore} works correctly after being serialized and deserialized back.
 */
class InMemoryEmbeddingStoreSerializedTest extends EmbeddingStoreWithFilteringIT {

    InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

    EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

    @Override
    protected EmbeddingStore<TextSegment> embeddingStore() {
        serializeAndDeserialize();
        return embeddingStore;
    }

    private void serializeAndDeserialize() {
        String json = embeddingStore.serializeToJson();
        embeddingStore = InMemoryEmbeddingStore.fromJson(json);
    }

    @Override
    protected EmbeddingModel embeddingModel() {
        return embeddingModel;
    }

    @Override
    protected boolean supportsContains() {
        return true;
    }
}
