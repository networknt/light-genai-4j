package com.networknt.agent.model.embedding;

import static com.networknt.agent.internal.Utils.isNullOrEmpty;

import com.networknt.agent.Internal;
import com.networknt.agent.model.embedding.listener.EmbeddingModelErrorContext;
import com.networknt.agent.model.embedding.listener.EmbeddingModelListener;
import com.networknt.agent.model.embedding.listener.EmbeddingModelRequestContext;
import com.networknt.agent.model.embedding.listener.EmbeddingModelResponseContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
class EmbeddingModelListenerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddingModelListenerUtils.class);

    private EmbeddingModelListenerUtils() {}

    static void onRequest(EmbeddingModelRequestContext requestContext, List<EmbeddingModelListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.onRequest(requestContext);
            } catch (Exception e) {
                LOG.warn(
                        "An exception occurred during the invocation of the embedding model listener. "
                                + "This exception has been ignored.",
                        e);
            }
        });
    }

    static void onResponse(EmbeddingModelResponseContext responseContext, List<EmbeddingModelListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.onResponse(responseContext);
            } catch (Exception e) {
                LOG.warn(
                        "An exception occurred during the invocation of the embedding model listener. "
                                + "This exception has been ignored.",
                        e);
            }
        });
    }

    static void onError(EmbeddingModelErrorContext errorContext, List<EmbeddingModelListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.onError(errorContext);
            } catch (Exception e) {
                LOG.warn(
                        "An exception occurred during the invocation of the embedding model listener. "
                                + "This exception has been ignored.",
                        e);
            }
        });
    }
}
