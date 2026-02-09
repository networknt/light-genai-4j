package com.networknt.agent.model.input;

import static com.networknt.agent.data.message.AiMessage.aiMessage;
import static com.networknt.agent.data.message.SystemMessage.systemMessage;
import static com.networknt.agent.data.message.UserMessage.userMessage;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class PromptTest implements WithAssertions {
    @Test
    void constructor() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Prompt(" "))
                .withMessageContaining("blank");

        Prompt p = new Prompt("abc");
        assertThat(p).hasToString("Prompt { text = \"abc\" }").isEqualTo(Prompt.from("abc"));
        assertThat(p.text()).isEqualTo("abc");

        assertThat(p.toSystemMessage()).isEqualTo(systemMessage("abc"));
        assertThat(p.toUserMessage()).isEqualTo(userMessage("abc"));
        assertThat(p.toUserMessage("userName")).isEqualTo(userMessage("userName", "abc"));
        assertThat(p.toAiMessage()).isEqualTo(aiMessage("abc"));
    }

    @Test
    void equals_hash_code() {
        Prompt p1 = Prompt.from("abc");
        Prompt p2 = Prompt.from("abc");

        Prompt p3 = Prompt.from("xyz");
        Prompt p4 = Prompt.from("xyz");

        assertThat(p1)
                .isNotEqualTo(null)
                .isNotEqualTo(new Object())
                .isEqualTo(p1)
                .hasSameHashCodeAs(p1)
                .isEqualTo(p2)
                .hasSameHashCodeAs(p2)
                .isNotEqualTo(p3)
                .doesNotHaveSameHashCodeAs(p3);

        assertThat(p3).isEqualTo(p3).isEqualTo(p4).hasSameHashCodeAs(p4);
    }
}
