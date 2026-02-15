package com.networknt.genai.spi.prompt.structured;

import com.networknt.genai.Internal;
import com.networknt.genai.model.input.Prompt;

/**
 * Represents a factory for structured prompts.
 */
@Internal
public interface StructuredPromptFactory {

    /**
     * Converts the given structured prompt to a prompt.
     * @param structuredPrompt the structured prompt.
     * @return the prompt.
     */
    Prompt toPrompt(Object structuredPrompt);
}
