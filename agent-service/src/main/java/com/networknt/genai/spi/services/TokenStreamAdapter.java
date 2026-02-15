package com.networknt.genai.spi.services;

import com.networknt.genai.Internal;
import com.networknt.genai.service.TokenStream;

import java.lang.reflect.Type;

@Internal
public interface TokenStreamAdapter {

    boolean canAdaptTokenStreamTo(Type type);

    Object adapt(TokenStream tokenStream);
}
