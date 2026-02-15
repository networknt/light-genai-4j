package com.networknt.genai.data.document.splitter;

import com.networknt.genai.data.document.Document;
import com.networknt.genai.data.document.DocumentSplitter;
import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.model.TokenCountEstimator;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * Splits the provided {@link Document} into parts using the provided {@code regex} and attempts to fit as many parts
 * as possible into a single {@link TextSegment}, adhering to the limit set by {@code maxSegmentSize}.
 * <p>
 * The {@code maxSegmentSize} can be defined in terms of characters (default) or tokens.
 * For token-based limit, a {@link TokenCountEstimator} must be provided.
 * <p>
 * If multiple parts fit within {@code maxSegmentSize}, they are joined together using the provided {@code joinDelimiter}.
 * <p>
 * If a single part is too long and exceeds {@code maxSegmentSize}, the {@code subSplitter} (which should be provided)
 * is used to split it into sub-parts and place them into multiple segments.
 * Such segments contain only the sub-parts of the split long part.
 * <p>
 * Each {@link TextSegment} inherits all metadata from the {@link Document} and includes an "index" metadata key
 * representing its position within the document (starting from 0).
 */
public class DocumentByRegexSplitter extends HierarchicalDocumentSplitter {

    private final String regex;
    private final String joinDelimiter;

    public DocumentByRegexSplitter(String regex,
                                   String joinDelimiter,
                                   int maxSegmentSizeInChars,
                                   int maxOverlapSizeInChars) {
        super(maxSegmentSizeInChars, maxOverlapSizeInChars, null, null);
        this.regex = ensureNotNull(regex, "regex");
        this.joinDelimiter = ensureNotNull(joinDelimiter, "joinDelimiter");
    }

    public DocumentByRegexSplitter(String regex,
                                   String joinDelimiter,
                                   int maxSegmentSizeInChars,
                                   int maxOverlapSizeInChars,
                                   DocumentSplitter subSplitter) {
        super(maxSegmentSizeInChars, maxOverlapSizeInChars, null, subSplitter);
        this.regex = ensureNotNull(regex, "regex");
        this.joinDelimiter = ensureNotNull(joinDelimiter, "joinDelimiter");
    }

    public DocumentByRegexSplitter(String regex,
                                   String joinDelimiter,
                                   int maxSegmentSizeInTokens,
                                   int maxOverlapSizeInTokens,
                                   TokenCountEstimator tokenCountEstimator) {
        super(maxSegmentSizeInTokens, maxOverlapSizeInTokens, tokenCountEstimator, null);
        this.regex = ensureNotNull(regex, "regex");
        this.joinDelimiter = ensureNotNull(joinDelimiter, "joinDelimiter");
    }

    public DocumentByRegexSplitter(String regex,
                                   String joinDelimiter,
                                   int maxSegmentSizeInTokens,
                                   int maxOverlapSizeInTokens,
                                   TokenCountEstimator tokenCountEstimator,
                                   DocumentSplitter subSplitter) {
        super(maxSegmentSizeInTokens, maxOverlapSizeInTokens, tokenCountEstimator, subSplitter);
        this.regex = ensureNotNull(regex, "regex");
        this.joinDelimiter = ensureNotNull(joinDelimiter, "joinDelimiter");
    }

    @Override
    public String[] split(String text) {
        return text.split(regex);
    }

    @Override
    public String joinDelimiter() {
        return joinDelimiter;
    }

    @Override
    protected DocumentSplitter defaultSubSplitter() {
        return null;
    }
}
