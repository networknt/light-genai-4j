package com.networknt.genai.observability.api.event;

import com.networknt.genai.invocation.InvocationContext;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.observability.event.DefaultAiServiceRequestIssuedEvent;
import com.networknt.genai.observability.event.DefaultAiServiceResponseReceivedEvent;

/**
 * Invoked just before a {@link com.networknt.genai.model.chat.request.ChatRequest} is sent.
 * It is important to note that this can be invoked multiple times during a single AI Service invocation
 * when tools or guardrails exist.
 */
public interface AiServiceRequestIssuedEvent extends AiServiceEvent {
    /**
     * Retrieves the chat request from the AI Service invocation event.
     *
     * @return the {@link ChatRequest} object containing the request sent to the LLM.
     */
    ChatRequest request();

    @Override
    default Class<AiServiceRequestIssuedEvent> eventClass() {
        return AiServiceRequestIssuedEvent.class;
    }

    @Override
    default AiServiceRequestIssuedEventBuilder toBuilder() {
        return new AiServiceRequestIssuedEventBuilder(this);
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    static AiServiceRequestIssuedEventBuilder builder() {
        return new AiServiceRequestIssuedEventBuilder();
    }

    /**
     * Builder for {@link DefaultAiServiceResponseReceivedEvent} instances.
     */
    class AiServiceRequestIssuedEventBuilder extends Builder<AiServiceRequestIssuedEvent> {
        private ChatRequest request;

        /**
         * Default constructor.
         */
        protected AiServiceRequestIssuedEventBuilder() {}

        /**
         * Creates a builder initialized from an existing {@link AiServiceRequestIssuedEvent}.
         *
         * @param src the source event
         */
        protected AiServiceRequestIssuedEventBuilder(AiServiceRequestIssuedEvent src) {
            super(src);
            request(src.request());
        }

        /**
         * Retrieves the chat request.
         *
         * @return the chat request
         */
        public ChatRequest request() {
            return request;
        }

        /**
         * Sets the invocation context.
         */
        public AiServiceRequestIssuedEventBuilder invocationContext(InvocationContext invocationContext) {
            return (AiServiceRequestIssuedEventBuilder) super.invocationContext(invocationContext);
        }

        /**
         * Sets the chat request.
         *
         * @param request the chat request
         * @return the builder
         */
        public AiServiceRequestIssuedEventBuilder request(ChatRequest request) {
            this.request = request;
            return this;
        }

        /**
         * Builds a {@link AiServiceRequestIssuedEvent}.
         */
        public AiServiceRequestIssuedEvent build() {
            return new DefaultAiServiceRequestIssuedEvent(this);
        }
    }
}
