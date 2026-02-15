package com.networknt.genai.chain;

import com.networknt.genai.service.AiServices;

/**
 * Represents a chain step that takes an input and produces an output.
 * <br>
 * Chains are not going to be developed further, it is recommended to use {@link AiServices} instead.
 *
 * @param <Input>  the input type
 * @param <Output> the output type
 */
@FunctionalInterface
public interface Chain<Input, Output> {

    /**
     * Execute the chain step.
     *
     * @param input the input
     * @return the output
     */
    Output execute(Input input);
}
