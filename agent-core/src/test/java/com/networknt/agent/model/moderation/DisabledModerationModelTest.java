package com.networknt.agent.model.moderation;

import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.DisabledModelTest;
import com.networknt.agent.model.input.Prompt;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class DisabledModerationModelTest extends DisabledModelTest<ModerationModel> {
    private ModerationModel model = new DisabledModerationModel();

    public DisabledModerationModelTest() {
        super(ModerationModel.class);
    }

    @Test
    void methodsShouldThrowException() {
        performAssertion(() -> this.model.moderate("Hello"));
        performAssertion(() -> this.model.moderate(Prompt.from("Hello")));
        performAssertion(() -> this.model.moderate((ChatMessage) null));
        performAssertion(() -> this.model.moderate(Collections.emptyList()));
        performAssertion(() -> this.model.moderate(TextSegment.textSegment("Hello")));
    }
}