package com.networknt.agent.service.output;

import com.networknt.agent.Internal;

@Internal
interface OutputParserFactory {

    OutputParser<?> get(Class<?> rawClass, Class<?> typeArgumentClass);
}
