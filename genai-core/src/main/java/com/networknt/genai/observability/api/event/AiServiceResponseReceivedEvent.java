package com.networknt.genai.observability.api.event;

import com.networknt.genai.invocation.InvocationContext;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.observability.event.DefaultAiServiceResponseReceivedEvent;

/**
 * Invoked when response from a {@link com.networknt.genai.model.chat.ChatModel} is received.
 * It is important to note that this can be invoked multiple times during a single AI Service invocation
 * when tools or guardrails exist.
 */
public interface AiServiceResponseReceivedEvent extends AiServiceEvent {

    /**
     * Retrieves the chat request from the AI Service invocation event.
     *
     * @return the {@link ChatRequest} object containing the request sent to the LLM.
     */
    ChatRequest request();

    /**
     * Retrieves the chat response from the AI Service invocation event.
     *
     * @return the {@link ChatResponse} object containing the AI-generated message and related metadata.
     */
    ChatResponse response();

    @Override
    default Class<AiServiceResponseReceivedEvent> eventClass() {
        return AiServiceResponseReceivedEvent.class;
    }

    @Override
    default AiServiceResponseReceivedEventBuilder toBuilder() {
        return new AiServiceResponseReceivedEventBuilder(this);
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    static AiServiceResponseReceivedEventBuilder builder() {
        return new AiServiceResponseReceivedEventBuilder();
    }

    /**
     * Builder for {@link DefaultAiServiceResponseReceivedEvent} instances.
     */
    class AiServiceResponseReceivedEventBuilder extends Builder<AiServiceResponseReceivedEvent> {
        private ChatResponse response;
        private ChatRequest request;

        /**
         * Default constructor.
         */
        protected AiServiceResponseReceivedEventBuilder() {}

        /**
         * Creates a builder initialized from an existing {@link AiServiceResponseReceivedEvent}.
         *
         * @param src the source event
         */
        protected AiServiceResponseReceivedEventBuilder(AiServiceResponseReceivedEvent src) {
            super(src);
            response(src.response());
            request(src.request());
        }

        /**
         * Retrieves the chat response.
         *
         * @return the chat response
         */
        public ChatResponse response() {
            return response;
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
        public AiServiceResponseReceivedEventBuilder invocationContext(InvocationContext invocationContext) {
            return (AiServiceResponseReceivedEventBuilder) super.invocationContext(invocationContext);
        }

        /**
         * Sets the chat request.
         *
         * @param request the chat request
         * @return the builder
         */
        public AiServiceResponseReceivedEventBuilder request(ChatRequest request) {
            this.request = request;
            return this;
        }

        /**
         * Sets the chat response.
         *
         * @param response the chat response
         * @return the builder
         */
        public AiServiceResponseReceivedEventBuilder response(ChatResponse response) {
            this.response = response;
            return this;
        }

        /**
         * Builds a {@link AiServiceResponseReceivedEvent}.
         */
        public AiServiceResponseReceivedEvent build() {
            return new DefaultAiServiceResponseReceivedEvent(this);
        }
    }
}
