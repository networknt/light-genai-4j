package com.networknt.agent.spi.guardrail.config;

import com.networknt.agent.guardrail.config.OutputGuardrailsConfig;
import java.util.function.Supplier;

/**
 * SPI for overriding and/or extending the default {@link OutputGuardrailsConfig.OutputGuardrailsConfigBuilder} implementation.
 */
public interface OutputGuardrailsConfigBuilderFactory
        extends Supplier<OutputGuardrailsConfig.OutputGuardrailsConfigBuilder> {}
