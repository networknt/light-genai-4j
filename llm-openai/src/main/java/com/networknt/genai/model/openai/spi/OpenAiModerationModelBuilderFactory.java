package com.networknt.genai.model.openai.spi;

import com.networknt.genai.Internal;
import com.networknt.genai.model.openai.OpenAiModerationModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OpenAiModerationModel.OpenAiModerationModelBuilder} instances.
 */
@Internal
public interface OpenAiModerationModelBuilderFactory extends Supplier<OpenAiModerationModel.OpenAiModerationModelBuilder> {
}
