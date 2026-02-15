package com.networknt.genai.model.openai.spi;

import com.networknt.genai.Internal;
import com.networknt.genai.model.openai.OpenAiEmbeddingModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link com.networknt.genai.model.openai.OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder} instances.
 */
@Internal
public interface OpenAiEmbeddingModelBuilderFactory extends Supplier<OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder> {
}
