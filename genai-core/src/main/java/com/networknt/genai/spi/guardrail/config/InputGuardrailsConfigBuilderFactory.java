package com.networknt.genai.spi.guardrail.config;

import com.networknt.genai.guardrail.config.InputGuardrailsConfig;
import java.util.function.Supplier;

/**
 * SPI for overriding and/or extending the default {@link InputGuardrailsConfig.InputGuardrailsConfigBuilder} implementation.
 */
public interface InputGuardrailsConfigBuilderFactory
        extends Supplier<InputGuardrailsConfig.InputGuardrailsConfigBuilder> {}
