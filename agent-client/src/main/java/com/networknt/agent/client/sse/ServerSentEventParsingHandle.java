package com.networknt.agent.client.sse;

import com.networknt.agent.Experimental;

/**
 * Handle that can be used to cancel the parsing of server-sent events.
 *
 * @since 1.8.0
 */
@Experimental
public interface ServerSentEventParsingHandle {

    /**
     * Cancels the parsing of server-sent events.
     */
    void cancel();

    /**
     * Returns {@code true} if parsing was cancelled by calling {@link #cancel()}.
     */
    boolean isCancelled();
}
