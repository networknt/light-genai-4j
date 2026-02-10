package com.networknt.model.ollama.spi;

import com.networknt.model.ollama.OllamaStreamingLanguageModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OllamaStreamingLanguageModel.OllamaStreamingLanguageModelBuilder} instances.
 */
public interface OllamaStreamingLanguageModelBuilderFactory extends Supplier<OllamaStreamingLanguageModel.OllamaStreamingLanguageModelBuilder> {
}
