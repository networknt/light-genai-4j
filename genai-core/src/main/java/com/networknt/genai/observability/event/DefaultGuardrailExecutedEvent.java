package com.networknt.genai.observability.event;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.guardrail.Guardrail;
import com.networknt.genai.guardrail.GuardrailRequest;
import com.networknt.genai.guardrail.GuardrailResult;
import com.networknt.genai.observability.api.event.GuardrailExecutedEvent;
import java.time.Duration;

/**
 * Represents an event that is executed when a guardrail validation occurs.
 * This interface serves as a marker for events that contain both parameters
 * and results associated with guardrail validation.
 *
 * @param <P> the type of guardrail parameters used in the validation process
 * @param <R> the type of guardrail result produced by the validation process
 * @param <G> the type of guardrail class used in the validation process
 * @param <E> the type of GuardrailExecutedEvent
 */
public abstract class DefaultGuardrailExecutedEvent<
                P extends GuardrailRequest<P>,
                R extends GuardrailResult<R>,
                G extends Guardrail<P, R>,
                E extends GuardrailExecutedEvent<P, R, G>>
        extends AbstractAiServiceEvent implements GuardrailExecutedEvent<P, R, G> {

    private final P request;
    private final R result;
    private final Class<G> guardrailClass;
    private final Duration duration;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    protected DefaultGuardrailExecutedEvent(GuardrailExecutedEventBuilder<P, R, G, E> builder) {
        super(builder);
        this.request = ensureNotNull(builder.request(), "request");
        this.result = ensureNotNull(builder.result(), "result");
        this.guardrailClass = ensureNotNull(builder.guardrailClass(), "guardrailClass");
        this.duration = ensureNotNull(builder.duration(), "duration");
    }

    /**
     * Returns the request.
     *
     * @return the request
     */
    @Override
    public P request() {
        return this.request;
    }

    /**
     * Returns the result.
     *
     * @return the result
     */
    @Override
    public R result() {
        return this.result;
    }

    /**
     * Returns the guardrail class.
     *
     * @return the guardrail class
     */
    @Override
    public Class<G> guardrailClass() {
        return this.guardrailClass;
    }

    /**
     * Returns the duration.
     *
     * @return the duration
     */
    @Override
    public Duration duration() {
        return this.duration;
    }
}
