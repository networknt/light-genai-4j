package com.networknt.agent.data.document.splitter;

import com.networknt.agent.data.document.Document;
import com.networknt.agent.data.document.DocumentSplitter;
import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.TokenCountEstimator;

/**
 * Splits the provided {@link Document} into characters and attempts to fit as many characters as possible
 * into a single {@link TextSegment}, adhering to the limit set by {@code maxSegmentSize}.
 * <p>
 * The {@code maxSegmentSize} can be defined in terms of characters (default) or tokens.
 * For token-based limit, a {@link TokenCountEstimator} must be provided.
 * <p>
 * If multiple characters fit within {@code maxSegmentSize}, they are joined together without delimiters.
 * <p>
 * Each {@link TextSegment} inherits all metadata from the {@link Document} and includes an "index" metadata key
 * representing its position within the document (starting from 0).
 */
public class DocumentByCharacterSplitter extends HierarchicalDocumentSplitter {

    public DocumentByCharacterSplitter(int maxSegmentSizeInChars,
                                       int maxOverlapSizeInChars) {
        super(maxSegmentSizeInChars, maxOverlapSizeInChars, null, null);
    }

    public DocumentByCharacterSplitter(int maxSegmentSizeInChars,
                                       int maxOverlapSizeInChars,
                                       DocumentSplitter subSplitter) {
        super(maxSegmentSizeInChars, maxOverlapSizeInChars, null, subSplitter);
    }

    public DocumentByCharacterSplitter(int maxSegmentSizeInTokens,
                                       int maxOverlapSizeInTokens,
                                       TokenCountEstimator tokenCountEstimator) {
        super(maxSegmentSizeInTokens, maxOverlapSizeInTokens, tokenCountEstimator, null);
    }

    public DocumentByCharacterSplitter(int maxSegmentSizeInTokens,
                                       int maxOverlapSizeInTokens,
                                       TokenCountEstimator tokenCountEstimator,
                                       DocumentSplitter subSplitter) {
        super(maxSegmentSizeInTokens, maxOverlapSizeInTokens, tokenCountEstimator, subSplitter);
    }

    @Override
    public String[] split(String text) {
        return text.split("");
    }

    @Override
    public String joinDelimiter() {
        return "";
    }

    @Override
    protected DocumentSplitter defaultSubSplitter() {
        return null;
    }
}
