package com.networknt.genai.observability.event;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.observability.api.event.AiServiceRequestIssuedEvent;

/**
 * Default implementation of {@link AiServiceRequestIssuedEvent}.
 */
public class DefaultAiServiceRequestIssuedEvent extends AbstractAiServiceEvent implements AiServiceRequestIssuedEvent {

    private final ChatRequest request;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public DefaultAiServiceRequestIssuedEvent(AiServiceRequestIssuedEventBuilder builder) {
        super(builder);
        this.request = ensureNotNull(builder.request(), "request");
    }

    /**
     * Returns the request.
     *
     * @return the request
     */
    @Override
    public ChatRequest request() {
        return request;
    }
}
