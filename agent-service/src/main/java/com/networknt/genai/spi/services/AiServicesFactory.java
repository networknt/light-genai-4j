package com.networknt.genai.spi.services;

import com.networknt.genai.Internal;
import com.networknt.genai.service.AiServiceContext;
import com.networknt.genai.service.AiServices;

@Internal
public interface AiServicesFactory {

    <T> AiServices<T> create(AiServiceContext context);
}
