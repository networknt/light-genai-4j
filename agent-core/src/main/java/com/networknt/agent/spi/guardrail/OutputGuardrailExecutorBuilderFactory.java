package com.networknt.agent.spi.guardrail;

import com.networknt.agent.guardrail.OutputGuardrail;
import com.networknt.agent.guardrail.OutputGuardrailExecutor;
import com.networknt.agent.guardrail.OutputGuardrailRequest;
import com.networknt.agent.guardrail.OutputGuardrailResult;
import com.networknt.agent.guardrail.config.OutputGuardrailsConfig;
import com.networknt.agent.observability.api.event.OutputGuardrailExecutedEvent;

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
