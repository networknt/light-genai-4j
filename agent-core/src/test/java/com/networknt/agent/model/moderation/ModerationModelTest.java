package com.networknt.agent.model.moderation;

import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.input.Prompt;
import com.networknt.agent.model.output.Response;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ModerationModelTest implements WithAssertions {

    public static class FlagEverythingModel implements ModerationModel {

        @Override
        public Response<Moderation> moderate(String text) {
            return Response.from(Moderation.flagged(text));
        }

        @Override
        public Response<Moderation> moderate(List<ChatMessage> messages) {
            return Response.from(Moderation.flagged(((UserMessage) messages.get(0)).singleText()));
        }
    }

    @Test
    void moderate_prompt() {
        ModerationModel model = new FlagEverythingModel();
        Response<Moderation> response = model.moderate(Prompt.from("Hello, world!"));
        assertThat(response).isEqualTo(Response.from(Moderation.flagged("Hello, world!")));
    }

    @Test
    void moderate_chat_message() {
        ModerationModel model = new FlagEverythingModel();
        Response<Moderation> response = model.moderate(UserMessage.from("Hello, world!"));
        assertThat(response).isEqualTo(Response.from(Moderation.flagged("Hello, world!")));
    }

    @Test
    void moderate_text_segment() {
        ModerationModel model = new FlagEverythingModel();
        Response<Moderation> response = model.moderate(TextSegment.from("Hello, world!"));
        assertThat(response).isEqualTo(Response.from(Moderation.flagged("Hello, world!")));
    }
}
