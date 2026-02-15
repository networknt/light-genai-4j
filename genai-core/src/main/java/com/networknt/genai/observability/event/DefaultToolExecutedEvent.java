package com.networknt.genai.observability.event;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.tool.ToolExecutionRequest;
import com.networknt.genai.observability.api.event.ToolExecutedEvent;

/**
 * Default implementation of {@link ToolExecutedEvent}.
 */
public class DefaultToolExecutedEvent extends AbstractAiServiceEvent implements ToolExecutedEvent {

    private final ToolExecutionRequest request;
    private final String resultText;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public DefaultToolExecutedEvent(ToolExecutedEventBuilder builder) {
        super(builder);
        this.request = ensureNotNull(builder.request(), "request");
        this.resultText = ensureNotNull(builder.resultText(), "resultText");
    }

    /**
     * Returns the request.
     *
     * @return the request
     */
    @Override
    public ToolExecutionRequest request() {
        return request;
    }

    /**
     * Returns the result text.
     *
     * @return the result text
     */
    @Override
    public String resultText() {
        return resultText;
    }
}
