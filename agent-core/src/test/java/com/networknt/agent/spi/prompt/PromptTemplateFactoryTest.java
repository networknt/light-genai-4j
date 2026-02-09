package com.networknt.agent.spi.prompt;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class PromptTemplateFactoryTest implements WithAssertions {
    @Test
    void input_defaults() {
        PromptTemplateFactory.Input input = () -> "template";
        assertThat(input.getName()).isEqualTo("template");
    }
}
