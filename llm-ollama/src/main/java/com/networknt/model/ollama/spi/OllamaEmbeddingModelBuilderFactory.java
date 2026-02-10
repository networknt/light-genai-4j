package com.networknt.model.ollama.spi;

import com.networknt.model.ollama.OllamaEmbeddingModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OllamaEmbeddingModel.OllamaEmbeddingModelBuilder} instances.
 */
public interface OllamaEmbeddingModelBuilderFactory extends Supplier<OllamaEmbeddingModel.OllamaEmbeddingModelBuilder> {
}
