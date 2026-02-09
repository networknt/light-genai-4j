package com.networknt.agent.store.embedding.inmemory;

import com.networknt.agent.Internal;
import com.networknt.agent.data.segment.TextSegment;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Internal
public interface InMemoryEmbeddingStoreJsonCodec {

    InMemoryEmbeddingStore<TextSegment> fromJson(String json);

    String toJson(InMemoryEmbeddingStore<?> store);

    default InMemoryEmbeddingStore<TextSegment> fromJson(InputStream in) throws IOException {
        String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        return fromJson(json);
    }

    default void toJson(OutputStream out, InMemoryEmbeddingStore<?> store) throws IOException {
        out.write(toJson(store).getBytes(StandardCharsets.UTF_8));
    }
}
