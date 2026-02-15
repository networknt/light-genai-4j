package com.networknt.genai.client.sse;

import com.networknt.genai.Internal;
import com.networknt.genai.model.chat.response.StreamingHandle;

/**
 * @since 1.8.0
 */
@Internal
public class ServerSentEventParsingHandleUtils {

    public static StreamingHandle toStreamingHandle(ServerSentEventParsingHandle parsingHandle) {
        return new StreamingHandle() {

            @Override
            public void cancel() {
                parsingHandle.cancel();
            }

            @Override
            public boolean isCancelled() {
                return parsingHandle.isCancelled();
            }
        };
    }
}
