package com.networknt.genai.model.azure.spi;

import com.networknt.genai.model.azure.AzureOpenAiAudioTranscriptionModel;
import java.util.function.Supplier;

/**
 * A factory for building {@link AzureOpenAiAudioTranscriptionModel.Builder} instances.
 * Used to provide custom builder implementations.
 */
public interface AzureOpenAiAudioTranscriptionModelBuilderFactory
        extends Supplier<AzureOpenAiAudioTranscriptionModel.Builder> {}
