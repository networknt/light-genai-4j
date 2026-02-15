package com.networknt.genai.service.output;

import com.networknt.genai.Internal;

import java.math.BigDecimal;

@Internal
class BigDecimalOutputParser implements OutputParser<BigDecimal> {

    @Override
    public BigDecimal parse(String string) {
        return new BigDecimal(string.trim());
    }

    @Override
    public String formatInstructions() {
        return "floating point number";
    }
}
