package com.networknt.agent.service.output;

import com.networknt.agent.Internal;

@Internal
class ShortOutputParser implements OutputParser<Short> {

    @Override
    public Short parse(String string) {
        return Short.parseShort(string.trim());
    }

    @Override
    public String formatInstructions() {
        return "integer number in range [-32768, 32767]";
    }
}
