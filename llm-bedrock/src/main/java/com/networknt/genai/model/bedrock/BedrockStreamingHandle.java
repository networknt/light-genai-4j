package com.networknt.genai.model.bedrock;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.model.chat.response.StreamingHandle;
import org.reactivestreams.Subscription;

/**
 * @since 1.8.0
 */
class BedrockStreamingHandle implements StreamingHandle {

    private final Subscription subscription;
    private volatile boolean isCancelled;

    BedrockStreamingHandle(Subscription subscription) {
        this.subscription = ensureNotNull(subscription, "subscription");
    }

    @Override
    public void cancel() {
        isCancelled = true;
        try {
            subscription.cancel();
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
}
