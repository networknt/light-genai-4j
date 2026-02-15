package com.networknt.genai.test.guardrail;

import com.networknt.genai.guardrail.InputGuardrailResult;
import com.networknt.genai.guardrail.InputGuardrailResult.Failure;

/**
 * Custom assertions for {@link InputGuardrailResult}s
 * <p>
 *     This follows the pattern described in https://assertj.github.io/doc/#assertj-core-custom-assertions-creation
 * </p>
 */
public final class InputGuardrailResultAssert
        extends GuardrailResultAssert<InputGuardrailResultAssert, InputGuardrailResult, Failure> {

    private InputGuardrailResultAssert(InputGuardrailResult inputGuardrailResult) {
        super(inputGuardrailResult, InputGuardrailResultAssert.class, Failure.class);
    }

    public static InputGuardrailResultAssert assertThat(InputGuardrailResult actual) {
        return new InputGuardrailResultAssert(actual);
    }
}
