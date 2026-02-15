package com.networknt.genai.observability.api.event;

import com.networknt.genai.invocation.InvocationContext;
import com.networknt.genai.observability.event.DefaultAiServiceErrorEvent;

/**
 * Represents an event that occurs when an AI Service invocation fails.
 * This interface extends {@link AiServiceEvent} to include additional information
 * about the error that caused the failure.
 *
 * Implementers of this interface can provide details about the failure, including the
 * associated {@link Throwable}, which can be used for debugging or logging purposes.
 */
public interface AiServiceErrorEvent extends AiServiceEvent {
    /**
     * Retrieves the {@link Throwable} representing the error associated with the AI Service invocation failure.
     *
     * @return the error
     */
    Throwable error();

    @Override
    default Class<AiServiceErrorEvent> eventClass() {
        return AiServiceErrorEvent.class;
    }

    @Override
    default AiServiceErrorEventBuilder toBuilder() {
        return new AiServiceErrorEventBuilder(this);
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    static AiServiceErrorEventBuilder builder() {
        return new AiServiceErrorEventBuilder();
    }

    /**
     * Builder for {@link DefaultAiServiceErrorEvent} instances.
     */
    class AiServiceErrorEventBuilder extends Builder<AiServiceErrorEvent> {
        private Throwable error;

        /**
         * Default constructor.
         */
        protected AiServiceErrorEventBuilder() {}

        /**
         * Creates a builder initialized from an existing {@link AiServiceErrorEvent}.
         *
         * @param src the source event
         */
        protected AiServiceErrorEventBuilder(AiServiceErrorEvent src) {
            super(src);
            error(src.error());
        }

        /**
         * Sets the invocation context.
         */
        public AiServiceErrorEventBuilder invocationContext(InvocationContext invocationContext) {
            return (AiServiceErrorEventBuilder) super.invocationContext(invocationContext);
        }

        /**
         * Sets the error.
         *
         * @param error the error
         * @return the builder
         */
        public AiServiceErrorEventBuilder error(Throwable error) {
            this.error = error;
            return this;
        }

        /**
         * Builds a {@link AiServiceErrorEvent}.
         */
        public AiServiceErrorEvent build() {
            return new DefaultAiServiceErrorEvent(this);
        }

        /**
         * Retrieves the error.
         *
         * @return the error
         */
        public Throwable getError() {
            return error;
        }
    }
}
