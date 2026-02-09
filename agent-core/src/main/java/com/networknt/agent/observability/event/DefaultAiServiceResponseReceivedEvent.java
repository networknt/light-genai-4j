package com.networknt.agent.observability.event;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

import com.networknt.agent.model.chat.request.ChatRequest;
import com.networknt.agent.model.chat.response.ChatResponse;
import com.networknt.agent.observability.api.event.AiServiceResponseReceivedEvent;

/**
 * Default implementation of {@link AiServiceResponseReceivedEvent}.
 */
public class DefaultAiServiceResponseReceivedEvent extends AbstractAiServiceEvent
        implements AiServiceResponseReceivedEvent {

    private final ChatResponse response;
    private final ChatRequest request;

    public DefaultAiServiceResponseReceivedEvent(AiServiceResponseReceivedEventBuilder builder) {
        super(builder);
        this.response = ensureNotNull(builder.response(), "response");
        this.request = ensureNotNull(builder.request(), "request");
    }

    @Override
    public ChatResponse response() {
        return response;
    }

    @Override
    public ChatRequest request() {
        return request;
    }
}
