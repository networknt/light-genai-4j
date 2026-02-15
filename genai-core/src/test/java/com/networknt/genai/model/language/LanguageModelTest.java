package com.networknt.genai.model.language;

import com.networknt.genai.model.input.Prompt;
import com.networknt.genai.model.output.Response;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class LanguageModelTest implements WithAssertions {
    public static class EchoLanguageModel implements LanguageModel {
        @Override
        public Response<String> generate(String prompt) {
            return new Response<>(prompt);
        }
    }

    @Test
    void generate() {
        LanguageModel model = new EchoLanguageModel();

        assertThat(model.generate(Prompt.from("text"))).isEqualTo(new Response<>("text"));
    }
}
