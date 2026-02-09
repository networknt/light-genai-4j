package com.networknt.agent.spi.services;

import com.networknt.agent.Internal;
import com.networknt.agent.service.AiServiceContext;

@Internal
public interface AiServiceContextFactory {

    AiServiceContext create(Class<?> aiServiceClass);
}
