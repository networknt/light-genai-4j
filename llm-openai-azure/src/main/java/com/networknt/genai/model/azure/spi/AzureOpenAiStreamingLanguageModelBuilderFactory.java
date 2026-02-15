package com.networknt.genai.model.azure.spi;

import com.networknt.genai.model.azure.AzureOpenAiStreamingLanguageModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link AzureOpenAiStreamingLanguageModel.Builder} instances.
 */
public interface AzureOpenAiStreamingLanguageModelBuilderFactory extends Supplier<AzureOpenAiStreamingLanguageModel.Builder> {
}
