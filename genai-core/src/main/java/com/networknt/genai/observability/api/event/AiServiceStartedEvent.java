package com.networknt.genai.observability.api.event;

import com.networknt.genai.data.message.SystemMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.invocation.InvocationContext;
import com.networknt.genai.observability.event.DefaultAiServiceStartedEvent;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * Called when an LLM invocation has started.
 */
public interface AiServiceStartedEvent extends AiServiceEvent {
    /**
     * Retrieves an optional system message associated with the invocation.
     * A system message typically provides instructions regarding the AI's
     * behavior, actions, or response style.
     *
     * @return an optional system message
     */
    Optional<SystemMessage> systemMessage();

    /**
     * Retrieves the user message associated with the invocation.
     * The user message represents the content or input provided by the user
     * during the AI Service invocation.
     *
     * @return the user message
     */
    UserMessage userMessage();

    /**
     * Creates a new builder instance for constructing a {@link AiServiceStartedEvent}.
     *
     * @return a new builder
     */
    static AiServiceStartedEventBuilder builder() {
        return new AiServiceStartedEventBuilder();
    }

    @Override
    default Class<AiServiceStartedEvent> eventClass() {
        return AiServiceStartedEvent.class;
    }

    @Override
    default AiServiceStartedEventBuilder toBuilder() {
        return new AiServiceStartedEventBuilder(this);
    }

    /**
     * Builder for {@link DefaultAiServiceStartedEvent} instances.
     */
    class AiServiceStartedEventBuilder extends Builder<AiServiceStartedEvent> {
        private @Nullable SystemMessage systemMessage;
        private UserMessage userMessage;

        /**
         * Default constructor.
         */
        protected AiServiceStartedEventBuilder() {}

        /**
         * Creates a builder initialized from an existing {@link AiServiceStartedEvent}.
         *
         * @param src the source event
         */
        protected AiServiceStartedEventBuilder(AiServiceStartedEvent src) {
            super(src);
            systemMessage(src.systemMessage().orElse(null));
            userMessage(src.userMessage());
        }

        /**
         * Sets the invocation context.
         */
        public AiServiceStartedEventBuilder invocationContext(InvocationContext invocationContext) {
            return (AiServiceStartedEventBuilder) super.invocationContext(invocationContext);
        }

        /**
         * Sets a system message.
         *
         * @param systemMessage the system message
         * @return the builder
         */
        public AiServiceStartedEventBuilder systemMessage(@Nullable SystemMessage systemMessage) {
            this.systemMessage = systemMessage;
            return this;
        }

        /**
         * Sets an optional system message.
         *
         * @param systemMessage the system message
         * @return the builder
         */
        public AiServiceStartedEventBuilder systemMessage(Optional<SystemMessage> systemMessage) {
            return systemMessage(systemMessage.orElse(null));
        }

        /**
         * Sets the user message.
         *
         * @param userMessage the user message
         * @return the builder
         */
        public AiServiceStartedEventBuilder userMessage(UserMessage userMessage) {
            this.userMessage = userMessage;
            return this;
        }

        /**
         * Builds a {@link AiServiceStartedEvent}.
         */
        public AiServiceStartedEvent build() {
            return new DefaultAiServiceStartedEvent(this);
        }

        /**
         * @return the system message
         */
        @Nullable
        public SystemMessage systemMessage() {
            return systemMessage;
        }

        /**
         * @return the user message
         */
        public UserMessage userMessage() {
            return userMessage;
        }
    }
}
