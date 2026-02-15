package com.networknt.genai.observability.event;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.observability.api.event.AiServiceErrorEvent;

/**
 * Default implementation of {@link AiServiceErrorEvent}.
 */
public class DefaultAiServiceErrorEvent extends AbstractAiServiceEvent implements AiServiceErrorEvent {

    private final Throwable error;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public DefaultAiServiceErrorEvent(AiServiceErrorEventBuilder builder) {
        super(builder);
        this.error = ensureNotNull(builder.getError(), "error");
    }

    /**
     * Returns the error.
     *
     * @return the error
     */
    @Override
    public Throwable error() {
        return error;
    }
}
