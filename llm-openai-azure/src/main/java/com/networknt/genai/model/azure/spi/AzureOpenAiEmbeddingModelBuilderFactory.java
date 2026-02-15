package com.networknt.genai.model.azure.spi;

import com.networknt.genai.model.azure.AzureOpenAiEmbeddingModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link AzureOpenAiEmbeddingModel.Builder} instances.
 */
public interface AzureOpenAiEmbeddingModelBuilderFactory extends Supplier<AzureOpenAiEmbeddingModel.Builder> {
}
