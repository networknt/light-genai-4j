package com.networknt.genai.model.ollama.spi;

import com.networknt.genai.model.ollama.OllamaLanguageModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OllamaLanguageModel.OllamaLanguageModelBuilder} instances.
 */
public interface OllamaLanguageModelBuilderFactory extends Supplier<OllamaLanguageModel.OllamaLanguageModelBuilder> {
}
