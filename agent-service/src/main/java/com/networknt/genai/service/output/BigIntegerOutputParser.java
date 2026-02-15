package com.networknt.genai.service.output;

import com.networknt.genai.Internal;

import java.math.BigInteger;

@Internal
class BigIntegerOutputParser implements OutputParser<BigInteger> {

    @Override
    public BigInteger parse(String string) {
        return new BigInteger(string.trim());
    }

    @Override
    public String formatInstructions() {
        return "integer number";
    }
}
