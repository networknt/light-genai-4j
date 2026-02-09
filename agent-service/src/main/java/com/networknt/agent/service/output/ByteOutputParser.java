package com.networknt.agent.service.output;

import com.networknt.agent.Internal;

@Internal
class ByteOutputParser implements OutputParser<Byte> {

    @Override
    public Byte parse(String string) {
        return Byte.parseByte(string.trim());
    }

    @Override
    public String formatInstructions() {
        return "integer number in range [-128, 127]";
    }
}
