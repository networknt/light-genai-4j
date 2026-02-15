package com.networknt.genai.observability.api.event;

import com.networknt.genai.invocation.InvocationContext;

/**
 * Represents an event that occurs during an AI Service invocation.
 */
public interface AiServiceEvent {
    /**
     * Retrieves the invocation context, containing general information
     * about where and how the invocation originated.
     *
     * @return the invocation context
     */
    InvocationContext invocationContext();

    /**
     * Retrieves the class type of the event, representing the specific category
     * of the AI Service invocation event.
     * <p>
     * Implementation note: I implemented it this way on purpose (rather than defining an enum of "Event Types")
     * So that downstream frameworks/applications could define their own event types and still use the
     * registration/firing mechanisms provided here in LC4j
     * </p>
     *
     * @param <T> the type of the event
     * @return the class of the event
     */
    <T extends AiServiceEvent> Class<T> eventClass();

    /**
     * Creates a new builder instance initialized with the properties of this {@link AiServiceEvent}.
     * This allows modification of the existing properties and reconstruction of the event.
     *
     * @param <T> the type of the event
     * @return a new builder instance
     */
    <T extends AiServiceEvent> Builder<T> toBuilder();

    /**
     * An abstract base class for building instances of types that extend {@link AiServiceEvent}.
     * This class provides a fluent interface for setting properties necessary
     * for constructing an {@link AiServiceEvent}.
     *
     * @param <T> the specific type of {@link AiServiceEvent} being built
     */
    abstract class Builder<T extends AiServiceEvent> {
        private InvocationContext invocationContext;

        /**
         * Default constructor.
         */
        protected Builder() {}

        /**
         * Copy constructor.
         *
         * @param src the source event
         */
        protected Builder(T src) {
            this.invocationContext = src.invocationContext();
        }

        /**
         * Retrieves the invocation context.
         *
         * @return the invocation context
         */
        public InvocationContext invocationContext() {
            return this.invocationContext;
        }

        /**
         * Sets the invocation context.
         *
         * @param invocationContext the invocation context
         * @return the builder
         */
        public Builder<T> invocationContext(InvocationContext invocationContext) {
            this.invocationContext = invocationContext;
            return this;
        }

        /**
         * Builds the event.
         *
         * @return the event
         */
        public abstract T build();
    }
}
