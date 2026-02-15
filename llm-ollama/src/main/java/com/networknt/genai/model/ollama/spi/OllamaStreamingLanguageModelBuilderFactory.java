package com.networknt.genai.model.ollama.spi;

import com.networknt.genai.model.ollama.OllamaStreamingLanguageModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OllamaStreamingLanguageModel.OllamaStreamingLanguageModelBuilder} instances.
 */
public interface OllamaStreamingLanguageModelBuilderFactory extends Supplier<OllamaStreamingLanguageModel.OllamaStreamingLanguageModelBuilder> {
}
