package com.networknt.agent.rag.content;

import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.rag.content.aggregator.ContentAggregator;
import com.networknt.agent.rag.content.injector.ContentInjector;
import com.networknt.agent.rag.content.retriever.ContentRetriever;
import com.networknt.agent.rag.query.Query;

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

    TextSegment textSegment();

    Map<ContentMetadata, Object> metadata();

    static Content from(String text) {
        return new DefaultContent(text);
    }

    static Content from(TextSegment textSegment) {
        return new DefaultContent(textSegment);
    }

    static Content from(TextSegment textSegment, Map<ContentMetadata, Object> metadata) {
        return new DefaultContent(textSegment, metadata);
    }
}
