package com.networknt.agent.spi.services;

import com.networknt.agent.Internal;
import com.networknt.agent.service.AiServiceContext;
import com.networknt.agent.service.AiServices;

@Internal
public interface AiServicesFactory {

    <T> AiServices<T> create(AiServiceContext context);
}
