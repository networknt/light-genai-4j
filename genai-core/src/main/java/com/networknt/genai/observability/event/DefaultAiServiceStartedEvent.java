package com.networknt.genai.observability.event;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.data.message.SystemMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.observability.api.event.AiServiceStartedEvent;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * Default implementation of {@link AiServiceStartedEvent}.
 */
public class DefaultAiServiceStartedEvent extends AbstractAiServiceEvent implements AiServiceStartedEvent {

    private final @Nullable SystemMessage systemMessage;
    private final UserMessage userMessage;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public DefaultAiServiceStartedEvent(AiServiceStartedEventBuilder builder) {
        super(builder);
        this.systemMessage = builder.systemMessage();
        this.userMessage = ensureNotNull(builder.userMessage(), "userMessage");
    }

    /**
     * Returns the system message.
     *
     * @return the system message
     */
    @Override
    public Optional<SystemMessage> systemMessage() {
        return Optional.ofNullable(this.systemMessage);
    }

    /**
     * Returns the user message.
     *
     * @return the user message
     */
    @Override
    public UserMessage userMessage() {
        return userMessage;
    }
}
