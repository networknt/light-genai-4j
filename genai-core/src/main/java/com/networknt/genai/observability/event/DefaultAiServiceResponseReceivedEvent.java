package com.networknt.genai.observability.event;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.observability.api.event.AiServiceResponseReceivedEvent;

/**
 * Default implementation of {@link AiServiceResponseReceivedEvent}.
 */
public class DefaultAiServiceResponseReceivedEvent extends AbstractAiServiceEvent
        implements AiServiceResponseReceivedEvent {

    private final ChatResponse response;
    private final ChatRequest request;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public DefaultAiServiceResponseReceivedEvent(AiServiceResponseReceivedEventBuilder builder) {
        super(builder);
        this.response = ensureNotNull(builder.response(), "response");
        this.request = ensureNotNull(builder.request(), "request");
    }

    /**
     * Returns the response.
     *
     * @return the response
     */
    @Override
    public ChatResponse response() {
        return response;
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
