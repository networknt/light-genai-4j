package com.networknt.genai.model.ollama.spi;

import com.networknt.genai.model.ollama.OllamaChatModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OllamaChatModel.OllamaChatModelBuilder} instances.
 */
public interface OllamaChatModelBuilderFactory extends Supplier<OllamaChatModel.OllamaChatModelBuilder> {
}
