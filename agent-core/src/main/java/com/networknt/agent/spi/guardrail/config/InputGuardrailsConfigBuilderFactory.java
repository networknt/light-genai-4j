package com.networknt.agent.spi.guardrail.config;

import com.networknt.agent.guardrail.config.InputGuardrailsConfig;
import java.util.function.Supplier;

/**
 * SPI for overriding and/or extending the default {@link InputGuardrailsConfig.InputGuardrailsConfigBuilder} implementation.
 */
public interface InputGuardrailsConfigBuilderFactory
        extends Supplier<InputGuardrailsConfig.InputGuardrailsConfigBuilder> {}
