package com.networknt.genai.client.sse;

import com.networknt.genai.Internal;
import com.networknt.genai.exception.UnsupportedFeatureException;

/**
 * @since 1.8.0
 */
@Internal
public class CancellationUnsupportedHandle implements ServerSentEventParsingHandle {

    @Override
    public void cancel() {
        throw new UnsupportedFeatureException("Streaming cancellation is not supported when calling "
                + "ServerSentEventListener.onEvent(ServerSentEvent). Please call "
                + "ServerSentEventListener.onEvent(ServerSentEvent, ServerSentEventContext) instead.");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
