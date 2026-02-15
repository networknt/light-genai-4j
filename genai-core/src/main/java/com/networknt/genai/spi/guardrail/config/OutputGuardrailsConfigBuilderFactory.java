package com.networknt.genai.spi.guardrail.config;

import com.networknt.genai.guardrail.config.OutputGuardrailsConfig;
import java.util.function.Supplier;

/**
 * SPI for overriding and/or extending the default {@link OutputGuardrailsConfig.OutputGuardrailsConfigBuilder} implementation.
 */
public interface OutputGuardrailsConfigBuilderFactory
        extends Supplier<OutputGuardrailsConfig.OutputGuardrailsConfigBuilder> {}
