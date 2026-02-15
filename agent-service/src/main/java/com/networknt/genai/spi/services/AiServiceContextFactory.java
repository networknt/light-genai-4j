package com.networknt.genai.spi.services;

import com.networknt.genai.Internal;
import com.networknt.genai.service.AiServiceContext;

@Internal
public interface AiServiceContextFactory {

    AiServiceContext create(Class<?> aiServiceClass);
}
