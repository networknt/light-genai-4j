package com.networknt.genai.spi.guardrail;

import com.networknt.genai.guardrail.OutputGuardrail;
import com.networknt.genai.guardrail.OutputGuardrailExecutor;
import com.networknt.genai.guardrail.OutputGuardrailRequest;
import com.networknt.genai.guardrail.OutputGuardrailResult;
import com.networknt.genai.guardrail.config.OutputGuardrailsConfig;
import com.networknt.genai.observability.api.event.OutputGuardrailExecutedEvent;

/**
 * Represents a factory for creating instances of {@link OutputGuardrailExecutor.OutputGuardrailExecutorBuilder}.
 * This interface extends {@link GuardrailExecutorBuilderFactory} and is specifically tailored for output guardrails,
 * utilizing configurations, results, requests, and guardrails that are specific to the output context.
 */
public non-sealed interface OutputGuardrailExecutorBuilderFactory
        extends GuardrailExecutorBuilderFactory<
                OutputGuardrailsConfig,
                OutputGuardrailResult,
                OutputGuardrailRequest,
                OutputGuardrail,
                OutputGuardrailExecutedEvent,
                OutputGuardrailExecutor.OutputGuardrailExecutorBuilder> {}
