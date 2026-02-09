package com.networknt.agent.spi.store.embedding.inmemory;

import com.networknt.agent.Internal;
import com.networknt.agent.store.embedding.inmemory.InMemoryEmbeddingStoreJsonCodec;

@Internal
public interface InMemoryEmbeddingStoreJsonCodecFactory {

    InMemoryEmbeddingStoreJsonCodec create();
}
