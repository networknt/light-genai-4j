package com.networknt.genai.spi.guardrail;

import com.networknt.genai.guardrail.AbstractGuardrailExecutor.GuardrailExecutorBuilder;
import com.networknt.genai.guardrail.Guardrail;
import com.networknt.genai.guardrail.GuardrailExecutor;
import com.networknt.genai.guardrail.GuardrailRequest;
import com.networknt.genai.guardrail.GuardrailResult;
import com.networknt.genai.guardrail.config.GuardrailsConfig;
import com.networknt.genai.observability.api.event.GuardrailExecutedEvent;

/**
 * Represents a factory for creating instances of {@link GuardrailExecutorBuilder}.
 * This interface is sealed and can only be extended by specific implementations like
 * {@code InputGuardrailExecutorBuilderFactory} and {@code OutputGuardrailExecutorBuilderFactory}.
 *
 * @param <C> the type of guardrails configuration, extending from {@link GuardrailsConfig}
 * @param <R> the type of guardrail result, extending from {@link GuardrailResult}
 * @param <P> the type of guardrail request, extending from {@link GuardrailRequest}
 * @param <G> the type of guardrail, extending from {@link Guardrail}
 * @param <E> the type of guardrail executed event, extending from {@link GuardrailExecutedEvent}
 * @param <B> the type of builder for creating {@link GuardrailExecutor}, extending from {@link GuardrailExecutorBuilder}
 */
public sealed interface GuardrailExecutorBuilderFactory<
                C extends GuardrailsConfig,
                R extends GuardrailResult<R>,
                P extends GuardrailRequest<P>,
                G extends Guardrail<P, R>,
                E extends GuardrailExecutedEvent<P, R, G>,
                B extends GuardrailExecutorBuilder<C, R, P, G, E, B>>
        permits InputGuardrailExecutorBuilderFactory, OutputGuardrailExecutorBuilderFactory {

    /**
     * Retrieves a builder for creating instances of {@link GuardrailExecutor}.
     * @return A new instance of type {@link B}, which is a builder extending from {@link GuardrailExecutorBuilder}.
     */
    B getBuilder();
}
