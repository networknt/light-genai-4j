package com.networknt.agent.observability.event;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

import com.networknt.agent.invocation.InvocationContext;
import com.networknt.agent.observability.api.event.AiServiceEvent;

public abstract class AbstractAiServiceEvent implements AiServiceEvent {
    private final InvocationContext invocationContext;

    protected AbstractAiServiceEvent(Builder<?> builder) {
        ensureNotNull(builder, "builder");
        this.invocationContext = ensureNotNull(builder.invocationContext(), "invocationContext");
    }

    @Override
    public InvocationContext invocationContext() {
        return this.invocationContext;
    }
}
