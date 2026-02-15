package com.networknt.genai.model.openai.spi;

import com.networknt.genai.Internal;
import com.networknt.genai.model.openai.OpenAiChatModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link com.networknt.genai.model.openai.OpenAiChatModel.OpenAiChatModelBuilder} instances.
 */
@Internal
public interface OpenAiChatModelBuilderFactory extends Supplier<OpenAiChatModel.OpenAiChatModelBuilder> {
}
