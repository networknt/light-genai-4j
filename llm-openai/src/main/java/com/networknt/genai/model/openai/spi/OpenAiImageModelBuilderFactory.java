package com.networknt.genai.model.openai.spi;

import com.networknt.genai.Internal;
import com.networknt.genai.model.openai.OpenAiImageModel;

import java.util.function.Supplier;

/**
 * A factory for building {@link OpenAiImageModel.OpenAiImageModelBuilder} instances.
 */
@Internal
public interface OpenAiImageModelBuilderFactory extends Supplier<OpenAiImageModel.OpenAiImageModelBuilder> {
}
