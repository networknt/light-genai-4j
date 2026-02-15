package com.networknt.genai.model.openai;

import com.networknt.genai.model.language.LanguageModel;
import com.networknt.genai.model.output.Response;
import com.networknt.genai.model.output.TokenUsage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static com.networknt.genai.model.openai.OpenAiLanguageModelName.GPT_3_5_TURBO_INSTRUCT;
import static com.networknt.genai.model.output.FinishReason.STOP;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiLanguageModelIT {

    LanguageModel model = OpenAiLanguageModel.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
            .modelName(GPT_3_5_TURBO_INSTRUCT)
            .logRequests(true)
            .logResponses(true)
            .build();

    @Test
    void should_generate_answer_and_return_token_usage_and_finish_reason_stop() {

        String prompt = "What is the capital of Germany?";

        Response<String> response = model.generate(prompt);

        assertThat(response.content()).contains("Berlin");

        TokenUsage tokenUsage = response.tokenUsage();
        assertThat(tokenUsage.inputTokenCount()).isPositive();
        assertThat(tokenUsage.outputTokenCount()).isPositive();
        assertThat(tokenUsage.totalTokenCount())
                .isEqualTo(tokenUsage.inputTokenCount() + tokenUsage.outputTokenCount());

        assertThat(response.finishReason()).isEqualTo(STOP);
    }
}
