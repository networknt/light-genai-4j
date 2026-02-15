package com.networknt.genai.model.embedding;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static com.networknt.genai.model.embedding.EmbeddingModelListenerUtils.onError;
import static com.networknt.genai.model.embedding.EmbeddingModelListenerUtils.onRequest;
import static com.networknt.genai.model.embedding.EmbeddingModelListenerUtils.onResponse;

import com.networknt.genai.Internal;
import com.networknt.genai.data.embedding.Embedding;
import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.model.embedding.listener.EmbeddingModelErrorContext;
import com.networknt.genai.model.embedding.listener.EmbeddingModelListener;
import com.networknt.genai.model.embedding.listener.EmbeddingModelRequestContext;
import com.networknt.genai.model.embedding.listener.EmbeddingModelResponseContext;
import com.networknt.genai.model.output.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Internal
final class ListeningEmbeddingModel implements EmbeddingModel {

    private final EmbeddingModel delegate;
    private final List<EmbeddingModelListener> listeners;

    ListeningEmbeddingModel(EmbeddingModel delegate, List<EmbeddingModelListener> listeners) {
        this.delegate = ensureNotNull(delegate, "delegate");
        this.listeners = copy(listeners);
    }

    EmbeddingModel withAdditionalListeners(List<EmbeddingModelListener> additionalListeners) {
        if (additionalListeners == null || additionalListeners.isEmpty()) {
            return this;
        }
        List<EmbeddingModelListener> merged = new ArrayList<>(listeners);
        merged.addAll(additionalListeners);
        return new ListeningEmbeddingModel(delegate, merged);
    }

    @Override
    public Response<Embedding> embed(String text) {
        Map<Object, Object> attributes = new ConcurrentHashMap<>();
        List<TextSegment> textSegmentsForContext;
        try {
            textSegmentsForContext = List.of(TextSegment.from(text));
        } catch (Exception ignored) {
            // keep behavior of delegate.embed(text) while still notifying listeners
            textSegmentsForContext = List.of();
        }

        EmbeddingModelRequestContext requestContext = EmbeddingModelRequestContext.builder()
                .textSegments(textSegmentsForContext)
                .embeddingModel(this)
                .attributes(attributes)
                .build();
        onRequest(requestContext, listeners);
        try {
            Response<Embedding> response = delegate.embed(text);

            Response<List<Embedding>> responseForListeners = Response.from(
                    Collections.singletonList(response.content()), response.tokenUsage(), response.finishReason());

            onResponse(
                    EmbeddingModelResponseContext.builder()
                            .response(responseForListeners)
                            .textSegments(textSegmentsForContext)
                            .embeddingModel(this)
                            .attributes(attributes)
                            .build(),
                    listeners);
            return response;
        } catch (Exception error) {
            onError(
                    EmbeddingModelErrorContext.builder()
                            .error(error)
                            .textSegments(textSegmentsForContext)
                            .embeddingModel(this)
                            .attributes(attributes)
                            .build(),
                    listeners);
            throw error;
        }
    }

    @Override
    public Response<Embedding> embed(TextSegment textSegment) {
        Map<Object, Object> attributes = new ConcurrentHashMap<>();
        List<TextSegment> textSegmentsForContext = (textSegment == null) ? List.of() : List.of(textSegment);

        EmbeddingModelRequestContext requestContext = EmbeddingModelRequestContext.builder()
                .textSegments(textSegmentsForContext)
                .embeddingModel(this)
                .attributes(attributes)
                .build();
        onRequest(requestContext, listeners);
        try {
            Response<Embedding> response = delegate.embed(textSegment);

            Response<List<Embedding>> responseForListeners = Response.from(
                    Collections.singletonList(response.content()), response.tokenUsage(), response.finishReason());

            onResponse(
                    EmbeddingModelResponseContext.builder()
                            .response(responseForListeners)
                            .textSegments(textSegmentsForContext)
                            .embeddingModel(this)
                            .attributes(attributes)
                            .build(),
                    listeners);
            return response;
        } catch (Exception error) {
            onError(
                    EmbeddingModelErrorContext.builder()
                            .error(error)
                            .textSegments(textSegmentsForContext)
                            .embeddingModel(this)
                            .attributes(attributes)
                            .build(),
                    listeners);
            throw error;
        }
    }

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        Map<Object, Object> attributes = new ConcurrentHashMap<>();
        EmbeddingModelRequestContext requestContext = EmbeddingModelRequestContext.builder()
                .textSegments(textSegments)
                .embeddingModel(this)
                .attributes(attributes)
                .build();
        onRequest(requestContext, listeners);
        try {
            Response<List<Embedding>> response = delegate.embedAll(textSegments);
            onResponse(
                    EmbeddingModelResponseContext.builder()
                            .response(response)
                            .textSegments(textSegments)
                            .embeddingModel(this)
                            .attributes(attributes)
                            .build(),
                    listeners);
            return response;
        } catch (Exception error) {
            onError(
                    EmbeddingModelErrorContext.builder()
                            .error(error)
                            .textSegments(textSegments)
                            .embeddingModel(this)
                            .attributes(attributes)
                            .build(),
                    listeners);
            throw error;
        }
    }

    @Override
    public String modelName() {
        return delegate.modelName();
    }
}
