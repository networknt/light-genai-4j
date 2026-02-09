package com.networknt.agent.spi.services;

import com.networknt.agent.Internal;
import com.networknt.agent.service.TokenStream;

import java.lang.reflect.Type;

@Internal
public interface TokenStreamAdapter {

    boolean canAdaptTokenStreamTo(Type type);

    Object adapt(TokenStream tokenStream);
}
