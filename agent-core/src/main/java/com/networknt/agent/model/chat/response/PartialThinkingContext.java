package com.networknt.agent.model.chat.response;

import com.networknt.agent.Experimental;
import com.networknt.agent.internal.JacocoIgnoreCoverageGenerated;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

/**
 * @since 1.8.0
 */
@Experimental
@JacocoIgnoreCoverageGenerated
public class PartialThinkingContext {

    private final StreamingHandle streamingHandle;

    public PartialThinkingContext(StreamingHandle streamingHandle) {
        this.streamingHandle = ensureNotNull(streamingHandle, "streamingHandle");
    }

    public StreamingHandle streamingHandle() {
        return streamingHandle;
    }
}
