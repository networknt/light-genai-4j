package com.networknt.genai.model.azure;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.model.chat.response.StreamingHandle;
import reactor.core.Disposable;

/**
 * @since 1.8.0
 */
class AzureOpenAiStreamingHandle implements StreamingHandle {

    private final Disposable disposable;
    private volatile boolean isCancelled;

    AzureOpenAiStreamingHandle(Disposable disposable) {
        this.disposable = ensureNotNull(disposable, "disposable");
    }

    @Override
    public void cancel() {
        isCancelled = true;
        try {
            disposable.dispose();
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }
}
