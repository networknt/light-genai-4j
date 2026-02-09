package com.networknt.agent.model.embedding;

import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.DisabledModelTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class DisabledEmbeddingModelTest extends DisabledModelTest<EmbeddingModel> {
    private EmbeddingModel model = new DisabledEmbeddingModel();

    public DisabledEmbeddingModelTest() {
        super(EmbeddingModel.class);
    }

    @Test
    void methodsShouldThrowException() {
        performAssertion(() -> this.model.embed("Hello"));
        performAssertion(() -> this.model.embed((TextSegment) null));
        performAssertion(() -> this.model.embedAll(Collections.emptyList()));
        performAssertion(() -> this.model.dimension());
    }
}