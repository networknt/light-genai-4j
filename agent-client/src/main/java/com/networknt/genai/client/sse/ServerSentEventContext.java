package com.networknt.genai.client.sse;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.Experimental;

/**
 * @since 1.8.0
 */
@Experimental
public class ServerSentEventContext {

    private final ServerSentEventParsingHandle parsingHandle;

    public ServerSentEventContext(ServerSentEventParsingHandle parsingHandle) {
        this.parsingHandle = ensureNotNull(parsingHandle, "parsingHandle");
    }

    public ServerSentEventParsingHandle parsingHandle() {
        return parsingHandle;
    }
}
