package com.networknt.genai.service.guardrail.spi;

import com.networknt.genai.service.guardrail.GuardrailService;

/**
 * A factory for providing instances of {@link GuardrailService.Builder}
 */
public interface GuardrailServiceBuilderFactory {
    /**
     * Gets an instance of the {@link GuardrailService.Builder}
     * @param aiServiceClass The class of the AI service
     * @return The {@link GuardrailService.Builder} instance
     */
    GuardrailService.Builder getBuilder(Class<?> aiServiceClass);
}
