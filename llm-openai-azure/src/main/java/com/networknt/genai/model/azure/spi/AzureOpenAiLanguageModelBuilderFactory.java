package com.networknt.genai.model.azure.spi;

import com.networknt.genai.model.azure.AzureOpenAiLanguageModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link AzureOpenAiLanguageModel.Builder} instances.
 */
public interface AzureOpenAiLanguageModelBuilderFactory extends Supplier<AzureOpenAiLanguageModel.Builder> {
}
