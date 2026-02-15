package com.networknt.genai.model.anthropic.internal.client;

import com.networknt.genai.Internal;

import java.util.function.Supplier;

@Internal
@SuppressWarnings("rawtypes")
public interface AnthropicClientBuilderFactory extends Supplier<AnthropicClient.Builder> {
}
