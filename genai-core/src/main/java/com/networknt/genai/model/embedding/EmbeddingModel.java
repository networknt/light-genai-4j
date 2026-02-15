package com.networknt.genai.model.embedding;

import static com.networknt.genai.internal.Utils.isNullOrEmpty;
import static java.util.Collections.singletonList;

import com.networknt.genai.Experimental;
import com.networknt.genai.data.embedding.Embedding;
import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.internal.ValidationUtils;
import com.networknt.genai.model.embedding.listener.EmbeddingModelListener;
import com.networknt.genai.model.output.Response;
import java.util.List;

/**
 * Represents a model that can convert a given text into an embedding (vector representation of the text).
 */
public interface EmbeddingModel {

    /**
     * Embed a text.
     *
     * @param text the text to embed.
     * @return the embedding.
     */
    default Response<Embedding> embed(String text) {
        return embed(TextSegment.from(text));
    }

    /**
     * Embed the text content of a TextSegment.
     *
     * @param textSegment the text segment to embed.
     * @return the embedding.
     */
    default Response<Embedding> embed(TextSegment textSegment) {
        Response<List<Embedding>> response = embedAll(singletonList(textSegment));
        ValidationUtils.ensureEq(
                response.content().size(),
                1,
                "Expected a single embedding, but got %d",
                response.content().size());
        return Response.from(response.content().get(0), response.tokenUsage(), response.finishReason());
    }

    /**
     * Embeds the text content of a list of TextSegments.
     *
     * @param textSegments the text segments to embed.
     * @return the embeddings.
     */
    Response<List<Embedding>> embedAll(List<TextSegment> textSegments);

    /**
     * Returns the dimension of the {@link Embedding} produced by this embedding model.
     *
     * @return dimension of the embedding
     */
    default int dimension() {
        return embed("test").content().dimension();
    }

    /**
     * Returns the name of the underlying embedding model.
     * <p>
     * Implementations are encouraged to override this method and provide the actual model name.
     * The default implementation returns {@code "unknown"}, which indicates
     * that the model name is unknown.
     *
     * @return the model name or a fallback value if not provided
     */
    default String modelName() {
        return "unknown";
    }

    /**
     * Wraps this {@link EmbeddingModel} with a listening model that dispatches events to the provided listener.
     *
     * @param listener The listener to add.
     * @return An observing {@link EmbeddingModel} that will dispatch events to the provided listener.
     * @since 1.11.0
     */
    @Experimental
    default EmbeddingModel addListener(EmbeddingModelListener listener) {
        return addListeners(listener == null ? null : List.of(listener));
    }

    /**
     * Wraps this {@link EmbeddingModel} with a listening model that dispatches events to the provided listeners.
     * <p>
     * Listeners are called in the order of iteration.
     *
     * @param listeners The listeners to add.
     * @return An observing {@link EmbeddingModel} that will dispatch events to the provided listeners.
     * @since 1.11.0
     */
    @Experimental
    default EmbeddingModel addListeners(List<EmbeddingModelListener> listeners) {
        if (isNullOrEmpty(listeners)) {
            return this;
        }
        if (this instanceof ListeningEmbeddingModel listeningEmbeddingModel) {
            return listeningEmbeddingModel.withAdditionalListeners(listeners);
        }
        return new ListeningEmbeddingModel(this, listeners);
    }
}
