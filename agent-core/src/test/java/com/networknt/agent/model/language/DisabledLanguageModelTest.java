package com.networknt.agent.model.language;

import com.networknt.agent.model.DisabledModelTest;
import com.networknt.agent.model.input.Prompt;
import org.junit.jupiter.api.Test;

class DisabledLanguageModelTest extends DisabledModelTest<LanguageModel> {
    private LanguageModel model = new DisabledLanguageModel();

    public DisabledLanguageModelTest() {
        super(LanguageModel.class);
    }

    @Test
    void methodsShouldThrowException() {
        performAssertion(() -> this.model.generate("Hello"));
        performAssertion(() -> this.model.generate(Prompt.from("Hello")));
    }
}