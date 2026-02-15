package com.networknt.genai.model.embedding.onnx;

import static com.networknt.genai.internal.Utils.repeat;
import static org.assertj.core.api.Assertions.assertThat;

import com.networknt.genai.model.TokenCountEstimator;
import org.junit.jupiter.api.Test;

class HuggingFaceTokenCountEstimatorTest {

    TokenCountEstimator tokenCountEstimator = new HuggingFaceTokenCountEstimator();

    @Test
    void should_count_tokens_in_text_shorter_than_512_tokens() {

        String text = "Hello, how are you doing?";

        int tokenCount = tokenCountEstimator.estimateTokenCountInText(text);

        assertThat(tokenCount).isEqualTo(7);
    }

    @Test
    void should_count_tokens_in_text_longer_than_512_tokens() {

        String text = repeat("Hello, how are you doing?", 100);

        int tokenCount = tokenCountEstimator.estimateTokenCountInText(text);

        assertThat(tokenCount).isEqualTo(700);
    }
}
