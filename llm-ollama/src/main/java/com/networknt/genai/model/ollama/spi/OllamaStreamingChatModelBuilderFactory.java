package com.networknt.genai.model.ollama.spi;

import com.networknt.genai.model.ollama.OllamaStreamingChatModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OllamaStreamingChatModel.OllamaStreamingChatModelBuilder} instances.
 */
public interface OllamaStreamingChatModelBuilderFactory extends Supplier<OllamaStreamingChatModel.OllamaStreamingChatModelBuilder> {
}
