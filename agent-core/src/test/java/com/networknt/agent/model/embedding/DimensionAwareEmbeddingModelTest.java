package com.networknt.agent.model.embedding;

import com.networknt.agent.data.embedding.Embedding;
import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.output.FinishReason;
import com.networknt.agent.model.output.Response;
import com.networknt.agent.model.output.TokenUsage;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class DimensionAwareEmbeddingModelTest implements WithAssertions {

    public static class DimensionAwareEmbeddingModelImpl extends DimensionAwareEmbeddingModel {

        final String modelName;

        DimensionAwareEmbeddingModelImpl(String modelName) {
            this.modelName = modelName;
        }

        DimensionAwareEmbeddingModelImpl(String modelName,
                                         Integer dimension) {
            this.modelName = modelName;
            this.dimension = dimension;
        }

        @Override
        public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
            List<Embedding> embeddings =
                    textSegments.stream().map(ts -> new Embedding(new float[]{ts.text().length(), ts.text().hashCode()}))
                            .collect(Collectors.toList());

            int tokenUsage = textSegments.stream().mapToInt(ts -> ts.text().length()).sum();

            return Response.from(embeddings, new TokenUsage(tokenUsage), FinishReason.STOP);
        }
    }

    @Test
    void should_return_correct_dimension_and_cached() {
        EmbeddingModel model = new DimensionAwareEmbeddingModelImpl("test-model");
        assertThat(model.dimension()).isEqualTo(2);

        // twice call model.dimension() should use cache result
        assertThat(model.dimension()).isEqualTo(2);
    }

    @Test
    void should_return_init_dimension() {
        // init class with dimension
        EmbeddingModel model = new DimensionAwareEmbeddingModelImpl("test-model", 5);
        assertThat(model.dimension()).isEqualTo(5);
    }
}
