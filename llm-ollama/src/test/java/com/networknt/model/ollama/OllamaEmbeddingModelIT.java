package com.networknt.model.ollama;

import com.networknt.agent.data.embedding.Embedding;
import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.model.output.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.networknt.model.ollama.AbstractOllamaLanguageModelInfrastructure.ollamaBaseUrl;
import static com.networknt.model.ollama.OllamaImage.ALL_MINILM_MODEL;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class OllamaEmbeddingModelIT extends AbstractOllamaEmbeddingModelInfrastructure {

    EmbeddingModel model = OllamaEmbeddingModel.builder()
            .baseUrl(ollamaBaseUrl(ollama))
            .modelName(ALL_MINILM_MODEL)
            .build();

    @Test
    void should_embed() {

        // given
        String text = "hello world";

        // when
        Response<Embedding> response = model.embed(text);

        // then
        assertThat(response.content().vector()).isNotEmpty();
        assertThat(response.content().dimension()).isEqualTo(model.dimension());

        assertThat(response.tokenUsage()).isNull();
        assertThat(response.finishReason()).isNull();
    }

    @Test
    void should_embed_multiple_segments() {

        // given
        List<TextSegment> segments = asList(
                TextSegment.from("hello"),
                TextSegment.from("world")
        );

        // when
        Response<List<Embedding>> response = model.embedAll(segments);

        // then
        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).dimension()).isEqualTo(model.dimension());
        assertThat(response.content().get(1).dimension()).isEqualTo(model.dimension());

        assertThat(response.tokenUsage()).isNull();
        assertThat(response.finishReason()).isNull();
    }

    @Test
    void should_return_correct_dimension() {
        // given
        String text = "hello world";

        // when
        Response<Embedding> response = model.embed(text);

        // then
        assertThat(model.dimension()).isEqualTo(response.content().dimension());
    }
}
