package com.networknt.genai.model.openai.spi;

import com.networknt.genai.Internal;
import com.networknt.genai.model.openai.OpenAiLanguageModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OpenAiLanguageModel.OpenAiLanguageModelBuilder} instances.
 */
@Internal
public interface OpenAiLanguageModelBuilderFactory extends Supplier<OpenAiLanguageModel.OpenAiLanguageModelBuilder> {
}
