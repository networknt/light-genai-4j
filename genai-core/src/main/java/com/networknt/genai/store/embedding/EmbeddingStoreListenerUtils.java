package com.networknt.genai.store.embedding;

import static com.networknt.genai.internal.Utils.isNullOrEmpty;

import com.networknt.genai.Internal;
import com.networknt.genai.store.embedding.listener.EmbeddingStoreErrorContext;
import com.networknt.genai.store.embedding.listener.EmbeddingStoreListener;
import com.networknt.genai.store.embedding.listener.EmbeddingStoreRequestContext;
import com.networknt.genai.store.embedding.listener.EmbeddingStoreResponseContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
class EmbeddingStoreListenerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddingStoreListenerUtils.class);

    private EmbeddingStoreListenerUtils() {}

    static void onRequest(EmbeddingStoreRequestContext<?> requestContext, List<EmbeddingStoreListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.onRequest(requestContext);
            } catch (Exception e) {
                LOG.warn(
                        "An exception occurred during the invocation of the embedding store listener. "
                                + "This exception has been ignored.",
                        e);
            }
        });
    }

    static void onResponse(EmbeddingStoreResponseContext<?> responseContext, List<EmbeddingStoreListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.onResponse(responseContext);
            } catch (Exception e) {
                LOG.warn(
                        "An exception occurred during the invocation of the embedding store listener. "
                                + "This exception has been ignored.",
                        e);
            }
        });
    }

    static void onError(EmbeddingStoreErrorContext<?> errorContext, List<EmbeddingStoreListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.onError(errorContext);
            } catch (Exception e) {
                LOG.warn(
                        "An exception occurred during the invocation of the embedding store listener. "
                                + "This exception has been ignored.",
                        e);
            }
        });
    }
}
