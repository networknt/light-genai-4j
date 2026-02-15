package com.networknt.genai.model.openaiofficial.microsoftfoundry;

import static com.networknt.genai.model.openaiofficial.microsoftfoundry.InternalMicrosoftFoundryTestHelper.EMBEDDING_MODEL_NAME;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.networknt.genai.data.embedding.Embedding;
import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.model.embedding.EmbeddingModel;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialEmbeddingModel;
import com.networknt.genai.model.output.Response;
import com.networknt.genai.model.output.TokenUsage;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "MICROSOFT_FOUNDRY_API_KEY", matches = ".+")
class MicrosoftFoundryEmbeddingModelIT {

    protected List<com.networknt.genai.model.embedding.EmbeddingModel> models() {
        return InternalMicrosoftFoundryTestHelper.embeddingModels();
    }

    @Test
    void should_embed_single_text() {
        for (EmbeddingModel model : models()) {
            // given
            String text = "hello world";

            // when
            Response<Embedding> response = model.embed(text);

            // then
            assertThat(response.content().vector()).hasSize(1536);

            TokenUsage tokenUsage = response.tokenUsage();
            assertThat(tokenUsage.inputTokenCount()).isEqualTo(2);
            assertThat(tokenUsage.outputTokenCount()).isNull();
            assertThat(tokenUsage.totalTokenCount()).isEqualTo(2);

            assertThat(response.finishReason()).isNull();
        }
    }

    @Test
    void should_embed_multiple_segments() {
        for (EmbeddingModel model : models()) {
            // given
            List<TextSegment> segments = asList(TextSegment.from("hello"), TextSegment.from("world"));

            // when
            Response<List<Embedding>> response = model.embedAll(segments);

            // then
            assertThat(response.content()).hasSize(2);
            assertThat(response.content().get(0).dimension()).isEqualTo(1536);
            assertThat(response.content().get(1).dimension()).isEqualTo(1536);

            TokenUsage tokenUsage = response.tokenUsage();
            assertThat(tokenUsage.inputTokenCount()).isEqualTo(2);
            assertThat(tokenUsage.outputTokenCount()).isNull();
            assertThat(tokenUsage.totalTokenCount()).isEqualTo(2);

            assertThat(response.finishReason()).isNull();
        }
    }

    @Test
    void should_embed_multiple_batch_segments() {
        // given
        int maxSegmentsPerBatch = 10;
        int totalSegmentsToEmbed = 50;

        EmbeddingModel model = OpenAiOfficialEmbeddingModel.builder()
                .baseUrl(System.getenv("MICROSOFT_FOUNDRY_ENDPOINT"))
                .apiKey(System.getenv("MICROSOFT_FOUNDRY_API_KEY"))
                .modelName(EMBEDDING_MODEL_NAME)
                .maxSegmentsPerBatch(maxSegmentsPerBatch)
                .build();

        List<TextSegment> segments = Stream.generate(() -> TextSegment.from("hello"))
                .limit(totalSegmentsToEmbed)
                .toList();

        // when
        Response<List<Embedding>> response = model.embedAll(segments);

        // then
        assertThat(response.content()).hasSize(totalSegmentsToEmbed);
        assertThat(response.content().get(0).dimension()).isEqualTo(1536);
        assertThat(response.content().get(10).dimension()).isEqualTo(1536);
        assertThat(response.content().get(20).dimension()).isEqualTo(1536);
        assertThat(response.content().get(30).dimension()).isEqualTo(1536);

        TokenUsage tokenUsage = response.tokenUsage();
        assertThat(tokenUsage.inputTokenCount()).isEqualTo(totalSegmentsToEmbed);
        assertThat(tokenUsage.outputTokenCount()).isNull();
        assertThat(tokenUsage.totalTokenCount()).isEqualTo(totalSegmentsToEmbed);

        assertThat(response.finishReason()).isNull();
    }

    @Test
    void should_embed_text_with_embedding_shortening() {

        // given
        int dimension = 42;

        EmbeddingModel model = OpenAiOfficialEmbeddingModel.builder()
                .baseUrl(System.getenv("MICROSOFT_FOUNDRY_ENDPOINT"))
                .apiKey(System.getenv("MICROSOFT_FOUNDRY_API_KEY"))
                .modelName(EMBEDDING_MODEL_NAME)
                .dimensions(dimension)
                .build();

        String text = "hello world";

        // when
        Response<Embedding> response = model.embed(text);

        // then
        assertThat(response.content().dimension()).isEqualTo(dimension);

        TokenUsage tokenUsage = response.tokenUsage();
        assertThat(tokenUsage.inputTokenCount()).isEqualTo(2);
        assertThat(tokenUsage.outputTokenCount()).isNull();
        assertThat(tokenUsage.totalTokenCount()).isEqualTo(2);

        assertThat(response.finishReason()).isNull();
    }
}
