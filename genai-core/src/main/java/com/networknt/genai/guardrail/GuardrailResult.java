package com.networknt.genai.guardrail;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The result of the validation of an interaction between a user and the LLM.
 *
 * @param <GR>
 *            The type of guardrail result to expect
 *
 * @see InputGuardrailResult
 * @see OutputGuardrailResult
 */
public sealed interface GuardrailResult<GR extends GuardrailResult<GR>>
        permits InputGuardrailResult, OutputGuardrailResult {
    /**
     * The possible results of a guardrails validation.
     */
    enum Result {
        /**
         * A successful validation.
         */
        SUCCESS,
        /**
         * A successful validation with a specific result.
         */
        SUCCESS_WITH_RESULT,
        /**
         * A failed validation not preventing the subsequent validations eventually registered to be evaluated.
         */
        FAILURE,
        /**
         * A fatal failed validation, blocking the evaluation of any other validations eventually registered.
         */
        FATAL
    }

    /**
     * The message and the cause of the failure of a single validation.
     */
    sealed interface Failure permits InputGuardrailResult.Failure, OutputGuardrailResult.Failure {
        /**
         * Build a failure from a specific {@link Guardrail} class.
         *
         * @param guardrailClass the guardrail class
         * @return the failure with the guardrail class set
         */
        Failure withGuardrailClass(Class<? extends Guardrail> guardrailClass);

        /**
         * The failure message.
         *
         * @return the failure message
         */
        String message();

        /**
         * The cause of the failure.
         *
         * @return the cause of the failure
         */
        Throwable cause();

        /**
         * The {@link Guardrail} class.
         *
         * @return the guardrail class
         */
        Class<? extends Guardrail> guardrailClass();

        /**
         * The string representation of the failure
         * @return A string representation of the failure
         */
        default String asString() {
            var guardrailName =
                    Optional.ofNullable(guardrailClass()).map(Class::getName).orElse("");

            return "The guardrail %s failed with this message: %s".formatted(guardrailName, message());
        }
    }

    /**
     * The result of the guardrail.
     *
     * @return the result
     */
    Result result();

    /**
     * The list of failures eventually resulting from a set of validations.
     *
     * @param <F> the type of failure
     * @return The list of failures eventually resulting from a set of validations.
     */
    <F extends Failure> List<F> failures();

    /**
     * The message of the successful result.
     *
     * @return the message of the successful result
     */
    String successfulText();

    /**
     * Whether or not the result is successful, but the result was re-written, potentially due to re-prompting.
     *
     * @return true if the result was rewritten, false otherwise
     */
    default boolean hasRewrittenResult() {
        return result() == Result.SUCCESS_WITH_RESULT;
    }

    /**
     * Whether or not the result is considered fatal.
     *
     * @return true if the result is fatal, false otherwise
     */
    default boolean isFatal() {
        return result() == Result.FATAL;
    }

    /**
     * Whether or not the result is considered successful.
     *
     * @return true if the result is successful, false otherwise
     */
    default boolean isSuccess() {
        var result = result();
        return (result == Result.SUCCESS) || (result == Result.SUCCESS_WITH_RESULT);
    }

    /**
     * Gets the exception from the first failure.
     *
     * @return the exception from the first failure, or null if there are no failures
     */
    default Throwable getFirstFailureException() {
        return !isSuccess()
                ? failures().stream()
                        .map(Failure::cause)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null)
                : null;
    }

    /**
     * The {@link Guardrail} class which performed this validation.
     *
     * @param guardrailClass the guardrail class
     * @return the guardrail result with the guardrail class set
     */
    default GR validatedBy(Class<? extends Guardrail> guardrailClass) {
        ensureNotNull(guardrailClass, "guardrailClass");

        if (!isSuccess()) {
            var failures = failures();

            if (failures.size() != 1) {
                throw new IllegalArgumentException();
            }

            failures.set(0, failures.get(0).withGuardrailClass(guardrailClass));
        }

        return (GR) this;
    }

    /**
     * Returns a string representation of the result.
     *
     * @return a string representation of the result
     */
    default String asString() {
        if (isSuccess()) {
            return hasRewrittenResult() ? "Success with '%s'".formatted(successfulText()) : "Success";
        }

        return failures().stream().map(Failure::toString).collect(Collectors.joining(", "));
    }
}
