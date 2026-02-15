package com.networknt.genai.model.openai.spi;

import com.networknt.genai.Internal;
import com.networknt.genai.model.openai.OpenAiAudioTranscriptionModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OpenAiAudioTranscriptionModel.Builder} instances.
 */
@Internal
public interface OpenAiAudioTranscriptionModelBuilderFactory extends Supplier<OpenAiAudioTranscriptionModel.Builder> {}
