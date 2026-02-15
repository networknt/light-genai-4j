package com.networknt.genai.model.azure.spi;

import com.networknt.genai.model.azure.AzureOpenAiImageModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link AzureOpenAiImageModel.Builder} instances.
 */
public interface AzureOpenAiImageModelBuilderFactory extends Supplier<AzureOpenAiImageModel.Builder> {
}
