package com.networknt.genai.spi.guardrail;

import com.networknt.genai.guardrail.InputGuardrail;
import com.networknt.genai.guardrail.InputGuardrailExecutor;
import com.networknt.genai.guardrail.InputGuardrailRequest;
import com.networknt.genai.guardrail.InputGuardrailResult;
import com.networknt.genai.guardrail.config.InputGuardrailsConfig;
import com.networknt.genai.observability.api.event.InputGuardrailExecutedEvent;

/**
 * Represents a factory for creating instances of {@link InputGuardrailExecutor.InputGuardrailExecutorBuilder}.
 * This non-sealed interface extends from the sealed interface {@link GuardrailExecutorBuilderFactory} and is specifically tailored
 * for input guardrails. It provides methods to configure and build execution environments for guardrails that operate on inputs,
 * ensuring that they adhere to predefined rules or constraints.
 */
public non-sealed interface InputGuardrailExecutorBuilderFactory
        extends GuardrailExecutorBuilderFactory<
                InputGuardrailsConfig,
                InputGuardrailResult,
                InputGuardrailRequest,
                InputGuardrail,
                InputGuardrailExecutedEvent,
                InputGuardrailExecutor.InputGuardrailExecutorBuilder> {}
