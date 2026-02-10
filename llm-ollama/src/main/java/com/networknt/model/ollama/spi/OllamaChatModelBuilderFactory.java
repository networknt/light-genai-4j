package com.networknt.model.ollama.spi;

import com.networknt.model.ollama.OllamaChatModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OllamaChatModel.OllamaChatModelBuilder} instances.
 */
public interface OllamaChatModelBuilderFactory extends Supplier<OllamaChatModel.OllamaChatModelBuilder> {
}
