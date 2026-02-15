package com.networknt.genai.rag.content.retriever;

import static com.networknt.genai.internal.Utils.isNullOrEmpty;

import com.networknt.genai.Internal;
import com.networknt.genai.rag.content.retriever.listener.ContentRetrieverErrorContext;
import com.networknt.genai.rag.content.retriever.listener.ContentRetrieverListener;
import com.networknt.genai.rag.content.retriever.listener.ContentRetrieverRequestContext;
import com.networknt.genai.rag.content.retriever.listener.ContentRetrieverResponseContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
class ContentRetrieverListenerUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ContentRetrieverListenerUtils.class);

    private ContentRetrieverListenerUtils() {}

    static void onRequest(ContentRetrieverRequestContext requestContext, List<ContentRetrieverListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.onRequest(requestContext);
            } catch (Exception e) {
                LOG.warn(
                        "An exception occurred during the invocation of the content retriever listener. "
                                + "This exception has been ignored.",
                        e);
            }
        });
    }

    static void onResponse(ContentRetrieverResponseContext responseContext, List<ContentRetrieverListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.onResponse(responseContext);
            } catch (Exception e) {
                LOG.warn(
                        "An exception occurred during the invocation of the content retriever listener. "
                                + "This exception has been ignored.",
                        e);
            }
        });
    }

    static void onError(ContentRetrieverErrorContext errorContext, List<ContentRetrieverListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return;
        }
        listeners.forEach(listener -> {
            try {
                listener.onError(errorContext);
            } catch (Exception e) {
                LOG.warn(
                        "An exception occurred during the invocation of the content retriever listener. "
                                + "This exception has been ignored.",
                        e);
            }
        });
    }
}
