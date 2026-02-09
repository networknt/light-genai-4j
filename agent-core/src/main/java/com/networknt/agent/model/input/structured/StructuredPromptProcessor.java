package com.networknt.agent.model.input.structured;

import com.networknt.agent.Internal;
import com.networknt.agent.model.input.Prompt;
import com.networknt.agent.spi.prompt.structured.StructuredPromptFactory;

import static com.networknt.agent.spi.ServiceHelper.loadFactories;

/**
 * Utility class for structured prompts.
 * Loads the {@link StructuredPromptFactory} SPI.
 */
@Internal
public class StructuredPromptProcessor {
    private StructuredPromptProcessor() {
    }

    private static final StructuredPromptFactory FACTORY = factory();

    private static StructuredPromptFactory factory() {
        for (StructuredPromptFactory factory : loadFactories(StructuredPromptFactory.class)) {
            return factory;
        }
        return new DefaultStructuredPromptFactory();
    }

    /**
     * Converts the given structured prompt to a prompt.
     *
     * @param structuredPrompt the structured prompt.
     * @return the prompt.
     */
    public static Prompt toPrompt(Object structuredPrompt) {
        return FACTORY.toPrompt(structuredPrompt);
    }
}
