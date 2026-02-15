package com.networknt.genai.observability.event;

import com.networknt.genai.observability.api.event.AiServiceCompletedEvent;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * Default implementation of {@link AiServiceCompletedEvent}.
 */
public class DefaultAiServiceCompletedEvent extends AbstractAiServiceEvent implements AiServiceCompletedEvent {

    private final @Nullable Object result;

    /**
     * Creates a new instance.
     *
     * @param builder the builder
     */
    public DefaultAiServiceCompletedEvent(AiServiceCompletedEventBuilder builder) {
        super(builder);
        this.result = builder.getResult();
    }

    /**
     * Returns the result.
     *
     * @return the result
     */
    @Override
    public Optional<Object> result() {
        return Optional.ofNullable(result);
    }
}
