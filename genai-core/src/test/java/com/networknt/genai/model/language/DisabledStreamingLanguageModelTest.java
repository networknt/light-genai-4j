package com.networknt.genai.model.language;

import com.networknt.genai.model.DisabledModelTest;
import com.networknt.genai.model.input.Prompt;
import org.junit.jupiter.api.Test;

class DisabledStreamingLanguageModelTest extends DisabledModelTest<StreamingLanguageModel> {
    private StreamingLanguageModel model = new DisabledStreamingLanguageModel();

    public DisabledStreamingLanguageModelTest() {
        super(StreamingLanguageModel.class);
    }

    @Test
    void methodsShouldThrowException() {
        performAssertion(() -> this.model.generate("Hello", null));
        performAssertion(() -> this.model.generate(Prompt.from("Hello"), null));
    }
}