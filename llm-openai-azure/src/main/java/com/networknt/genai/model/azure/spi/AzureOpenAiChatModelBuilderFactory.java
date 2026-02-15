package com.networknt.genai.model.azure.spi;

import com.networknt.genai.model.azure.AzureOpenAiChatModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link AzureOpenAiChatModel.Builder} instances.
 */
public interface AzureOpenAiChatModelBuilderFactory extends Supplier<AzureOpenAiChatModel.Builder> {
}
