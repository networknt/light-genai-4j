package com.networknt.genai.service.output;

import com.networknt.genai.Internal;

@Internal
interface OutputParserFactory {

    OutputParser<?> get(Class<?> rawClass, Class<?> typeArgumentClass);
}
