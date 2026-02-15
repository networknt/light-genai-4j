package com.networknt.genai.observability.api.listener;

import com.networknt.genai.guardrail.Guardrail;
import com.networknt.genai.guardrail.GuardrailRequest;
import com.networknt.genai.guardrail.GuardrailResult;
import com.networknt.genai.observability.api.event.GuardrailExecutedEvent;

/**
 * Represents a listener for events of type {@link GuardrailExecutedEvent}, which are triggered when a guardrail
 * validation is executed. This listener provides the ability to handle events that encapsulate the parameters,
 * results, and the guardrail class involved in the validation process.
 *
 * The interface is parameterized with the following types:
 *
 * @param <P> the type of {@link GuardrailRequest} used in the guardrail validation process. It represents
 *            the request or parameters supplied for validation.
 * @param <R> the type of {@link GuardrailResult} generated as the outcome of the validation process. It includes
 *            details regarding the success or failure of the validation and any associated information.
 * @param <G> the type of {@link Guardrail} used in the validation process. It represents the implementation of
 *            the specific validation logic being applied.
 * @param <E> the type of {@link GuardrailExecutedEvent}
 */
public sealed interface GuardrailExecutedListener<
                E extends GuardrailExecutedEvent<P, R, G>,
                P extends GuardrailRequest<P>,
                R extends GuardrailResult<R>,
                G extends Guardrail<P, R>>
        extends AiServiceListener<E> permits InputGuardrailExecutedListener, OutputGuardrailExecutedListener {}
