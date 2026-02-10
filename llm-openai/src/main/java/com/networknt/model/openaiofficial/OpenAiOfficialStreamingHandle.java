package com.networknt.model.openaiofficial;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

import com.openai.core.http.AsyncStreamResponse;
import com.networknt.agent.model.chat.response.StreamingHandle;

/**
 * @since 1.8.0
 */
class OpenAiOfficialStreamingHandle implements StreamingHandle {

    private final AsyncStreamResponse<?> asyncStreamResponse;
    private volatile boolean isCancelled;

    OpenAiOfficialStreamingHandle(AsyncStreamResponse<?> asyncStreamResponse) {
        this.asyncStreamResponse = ensureNotNull(asyncStreamResponse, "asyncStreamResponse");
    }

    @Override
    public void cancel() {
        isCancelled = true;
        try {
            asyncStreamResponse.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
}
