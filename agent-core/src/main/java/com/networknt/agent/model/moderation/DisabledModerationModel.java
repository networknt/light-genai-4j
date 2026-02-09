package com.networknt.agent.model.moderation;

import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.ModelDisabledException;
import com.networknt.agent.model.input.Prompt;
import com.networknt.agent.model.output.Response;

import java.util.List;

/**
 * A {@link ModerationModel} which throws a {@link ModelDisabledException} for all of its methods
 * <p>
 *     This could be used in tests, or in libraries that extend this one to conditionally enable or disable functionality.
 * </p>
 */
public class DisabledModerationModel implements ModerationModel {
    @Override
    public Response<Moderation> moderate(String text) {
        throw new ModelDisabledException("ModerationModel is disabled");
    }

    @Override
    public Response<Moderation> moderate(Prompt prompt) {
        throw new ModelDisabledException("ModerationModel is disabled");
    }

    @Override
    public Response<Moderation> moderate(ChatMessage message) {
        throw new ModelDisabledException("ModerationModel is disabled");
    }

    @Override
    public Response<Moderation> moderate(List<ChatMessage> messages) {
        throw new ModelDisabledException("ModerationModel is disabled");
    }

    @Override
    public Response<Moderation> moderate(TextSegment textSegment) {
        throw new ModelDisabledException("ModerationModel is disabled");
    }
}
