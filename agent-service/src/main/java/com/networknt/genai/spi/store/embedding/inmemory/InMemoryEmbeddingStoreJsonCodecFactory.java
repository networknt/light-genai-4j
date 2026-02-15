package com.networknt.genai.spi.store.embedding.inmemory;

import com.networknt.genai.Internal;
import com.networknt.genai.store.embedding.inmemory.InMemoryEmbeddingStoreJsonCodec;

@Internal
public interface InMemoryEmbeddingStoreJsonCodecFactory {

    InMemoryEmbeddingStoreJsonCodec create();
}
