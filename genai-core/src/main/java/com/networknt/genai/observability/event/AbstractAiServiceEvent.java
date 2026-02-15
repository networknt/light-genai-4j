package com.networknt.genai.observability.event;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.invocation.InvocationContext;
import com.networknt.genai.observability.api.event.AiServiceEvent;

/**
 * Abstract implementation of {@link AiServiceEvent}.
 */
public abstract class AbstractAiServiceEvent implements AiServiceEvent {
    private final InvocationContext invocationContext;

    /**
     * Constructor.
     *
     * @param builder the builder
     */
    protected AbstractAiServiceEvent(Builder<?> builder) {
        ensureNotNull(builder, "builder");
        this.invocationContext = ensureNotNull(builder.invocationContext(), "invocationContext");
    }

    /**
     * Returns the invocation context.
     *
     * @return the invocation context
     */
    @Override
    public InvocationContext invocationContext() {
        return this.invocationContext;
    }
}
