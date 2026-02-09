package com.networknt.agent.client.sse;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

import com.networknt.agent.Experimental;

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
