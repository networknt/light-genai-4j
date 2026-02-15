package com.networknt.genai.model.chat.response;

import com.networknt.genai.Experimental;
import com.networknt.genai.internal.JacocoIgnoreCoverageGenerated;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * Context for partial response.
 *
 * @since 1.8.0
 */
@Experimental
@JacocoIgnoreCoverageGenerated
public class PartialResponseContext {

    private final StreamingHandle streamingHandle;

    public PartialResponseContext(StreamingHandle streamingHandle) {
        this.streamingHandle = ensureNotNull(streamingHandle, "streamingHandle");
    }

    public StreamingHandle streamingHandle() {
        return streamingHandle;
    }
}
