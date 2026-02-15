package com.networknt.genai.model.openai.spi;

import com.networknt.genai.Internal;
import com.networknt.genai.model.openai.OpenAiStreamingChatModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder} instances.
 */
@Internal
public interface OpenAiStreamingChatModelBuilderFactory extends Supplier<OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder> {
}
