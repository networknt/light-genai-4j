package com.networknt.genai.model.openai.spi;

import com.networknt.genai.Internal;
import com.networknt.genai.model.openai.OpenAiStreamingLanguageModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OpenAiStreamingLanguageModel.OpenAiStreamingLanguageModelBuilder} instances.
 */
@Internal
public interface OpenAiStreamingLanguageModelBuilderFactory extends Supplier<OpenAiStreamingLanguageModel.OpenAiStreamingLanguageModelBuilder> {
}
