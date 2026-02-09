package com.networknt.agent.observability.event;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

import com.networknt.agent.tool.ToolExecutionRequest;
import com.networknt.agent.observability.api.event.ToolExecutedEvent;

/**
 * Default implementation of {@link ToolExecutedEvent}.
 */
public class DefaultToolExecutedEvent extends AbstractAiServiceEvent implements ToolExecutedEvent {

    private final ToolExecutionRequest request;
    private final String resultText;

    public DefaultToolExecutedEvent(ToolExecutedEventBuilder builder) {
        super(builder);
        this.request = ensureNotNull(builder.request(), "request");
        this.resultText = ensureNotNull(builder.resultText(), "resultText");
    }

    @Override
    public ToolExecutionRequest request() {
        return request;
    }

    @Override
    public String resultText() {
        return resultText;
    }
}
