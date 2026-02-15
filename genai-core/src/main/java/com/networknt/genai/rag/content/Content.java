package com.networknt.genai.rag.content;

import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.rag.content.aggregator.ContentAggregator;
import com.networknt.genai.rag.content.injector.ContentInjector;
import com.networknt.genai.rag.content.retriever.ContentRetriever;
import com.networknt.genai.rag.query.Query;

import java.util.Map;

/**
 * Represents content relevant to a user {@link Query} with the potential to enhance and ground the LLM's response.
 * <br>
 * Currently, it is limited to text content (i.e., {@link TextSegment}),
 * but future expansions may include support for other modalities (e.g., images, audio, video, etc.).
 *
 * @see ContentRetriever
 * @see ContentAggregator
 * @see ContentInjector
 */
public interface Content {

    /**
     * Returns the text segment.
     *
     * @return the text segment
     */
    TextSegment textSegment();

    /**
     * Returns the metadata.
     *
     * @return the metadata
     */
    Map<ContentMetadata, Object> metadata();

    /**
     * Creates a content from text.
     *
     * @param text the text
     * @return the content
     */
    static Content from(String text) {
        return new DefaultContent(text);
    }

    /**
     * Creates a content from a text segment.
     *
     * @param textSegment the text segment
     * @return the content
     */
    static Content from(TextSegment textSegment) {
        return new DefaultContent(textSegment);
    }

    /**
     * Creates a content from a text segment and metadata.
     *
     * @param textSegment the text segment
     * @param metadata    the metadata
     * @return the content
     */
    static Content from(TextSegment textSegment, Map<ContentMetadata, Object> metadata) {
        return new DefaultContent(textSegment, metadata);
    }
}
