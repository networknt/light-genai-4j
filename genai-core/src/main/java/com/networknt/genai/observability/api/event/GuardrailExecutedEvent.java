package com.networknt.genai.observability.api.event;

import com.networknt.genai.guardrail.Guardrail;
import com.networknt.genai.guardrail.GuardrailRequest;
import com.networknt.genai.guardrail.GuardrailResult;
import com.networknt.genai.invocation.InvocationContext;
import java.time.Duration;

/**
 * Represents an event that is executed when a guardrail validation occurs.
 * This interface serves as a marker for events that contain both parameters
 * and results associated with guardrail validation.
 *
 * @param <P> the type of guardrail parameters used in the validation process
 * @param <R> the type of guardrail result produced by the validation process
 * @param <G> the type of guardrail class used in the validation process
 */
public interface GuardrailExecutedEvent<
                P extends GuardrailRequest<P>, R extends GuardrailResult<R>, G extends Guardrail<P, R>>
        extends AiServiceEvent {

    /**
     * Retrieves the request used for input guardrail validation.
     *
     * @return the parameters containing user message, memory, augmentation result, user message template,
     *         and associated variables for input guardrail validation.
     */
    P request();

    /**
     * Retrieves the result of the input guardrail validation process.
     *
     * @return the result of the input guardrail validation, including the validation outcome
     *         and any associated failures, if present.
     */
    R result();

    /**
     * Retrieves the guardrail class associated with the validation process.
     *
     * @return the guardrail class that implements the logic for validating
     *         the interaction between user and LLM, represented as an instance
     *         of the type extending {@code Guardrail<P, R>}.
     */
    Class<G> guardrailClass();

    /**
     * Retrieves the duration of the guardrail execution.
     *
     * @return the duration of the guardrail validation process.
     */
    Duration duration();

    /**
     * Builder for {@link GuardrailExecutedEvent}.
     *
     * @param <P> the type of guardrail parameters
     * @param <R> the type of guardrail result
     * @param <G> the type of guardrail class
     * @param <T> the type of guardrail executed event
     */
    abstract class GuardrailExecutedEventBuilder<
                    P extends GuardrailRequest<P>,
                    R extends GuardrailResult<R>,
                    G extends Guardrail<P, R>,
                    T extends GuardrailExecutedEvent<P, R, G>>
            extends Builder<T> {

        private P request;
        private R result;
        private Class<G> guardrailClass;
        private Duration duration;

        /**
         * Creates a new builder.
         */
        protected GuardrailExecutedEventBuilder() {}

        /**
         * Creates a new builder from the source.
         *
         * @param src the source
         */
        protected GuardrailExecutedEventBuilder(T src) {
            super(src);
            request(src.request());
            result(src.result());
            guardrailClass(src.guardrailClass());
            duration(src.duration());
        }

        /**
         * Returns the guardrail class.
         *
         * @return the guardrail class
         */
        public Class<G> guardrailClass() {
            return guardrailClass;
        }

        /**
         * Returns the request.
         *
         * @return the request
         */
        public P request() {
            return request;
        }

        /**
         * Returns the result.
         *
         * @return the result
         */
        public R result() {
            return result;
        }

        /**
         * Returns the duration.
         *
         * @return the duration
         */
        public Duration duration() {
            return duration;
        }

        /**
         * Sets the request.
         *
         * @param request the request
         * @return the builder
         */
        public GuardrailExecutedEventBuilder<P, R, G, T> request(P request) {
            this.request = request;
            return this;
        }

        /**
         * Sets the result.
         *
         * @param result the result
         * @return the builder
         */
        public GuardrailExecutedEventBuilder<P, R, G, T> result(R result) {
            this.result = result;
            return this;
        }

        /**
         * Sets the invocation context.
         *
         * @param invocationContext the invocation context
         * @return the builder
         */
        public GuardrailExecutedEventBuilder<P, R, G, T> invocationContext(InvocationContext invocationContext) {
            return (GuardrailExecutedEventBuilder<P, R, G, T>) super.invocationContext(invocationContext);
        }

        /**
         * Sets the guardrail class.
         *
         * @param guardrailClass the guardrail class
         * @param <C> the type of guardrail class
         * @return the builder
         */
        public <C extends G> GuardrailExecutedEventBuilder<P, R, G, T> guardrailClass(Class<C> guardrailClass) {
            this.guardrailClass = (Class<G>) guardrailClass;
            return this;
        }

        /**
         * Sets the duration.
         *
         * @param duration the duration
         * @return the builder
         */
        public GuardrailExecutedEventBuilder<P, R, G, T> duration(Duration duration) {
            this.duration = duration;
            return this;
        }
    }
}
