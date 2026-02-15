package com.networknt.genai.model.azure.spi;

import com.networknt.genai.model.azure.AzureOpenAiStreamingChatModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link AzureOpenAiStreamingChatModel.Builder} instances.
 */
public interface AzureOpenAiStreamingChatModelBuilderFactory extends Supplier<AzureOpenAiStreamingChatModel.Builder> {
}
