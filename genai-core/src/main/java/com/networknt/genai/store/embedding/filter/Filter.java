package com.networknt.genai.store.embedding.filter;

import com.networknt.genai.internal.JacocoIgnoreCoverageGenerated;
import com.networknt.genai.store.embedding.EmbeddingStore;
import com.networknt.genai.store.embedding.filter.comparison.ContainsString;
import com.networknt.genai.store.embedding.filter.comparison.IsEqualTo;
import com.networknt.genai.store.embedding.filter.comparison.IsGreaterThan;
import com.networknt.genai.store.embedding.filter.comparison.IsGreaterThanOrEqualTo;
import com.networknt.genai.store.embedding.filter.comparison.IsIn;
import com.networknt.genai.store.embedding.filter.comparison.IsLessThan;
import com.networknt.genai.store.embedding.filter.comparison.IsLessThanOrEqualTo;
import com.networknt.genai.store.embedding.filter.comparison.IsNotEqualTo;
import com.networknt.genai.store.embedding.filter.comparison.IsNotIn;
import com.networknt.genai.store.embedding.filter.logical.And;
import com.networknt.genai.store.embedding.filter.logical.Not;
import com.networknt.genai.store.embedding.filter.logical.Or;

/**
 * This class represents a filter that can be applied during search in an {@link EmbeddingStore}.
 * <br>
 * Many {@link EmbeddingStore}s support a feature called metadata filtering. A {@code Filter} can be used for this.
 * <br>
 * A {@code Filter} object can represent simple (e.g. {@code type = 'documentation'})
 * and composite (e.g. {@code type = 'documentation' AND year > 2020}) filter expressions in
 * an {@link EmbeddingStore}-agnostic way.
 * <br>
 * Each {@link EmbeddingStore} implementation that supports metadata filtering is mapping {@link Filter}
 * into it's native filter expression.
 *
 * @see IsEqualTo
 * @see IsNotEqualTo
 * @see IsGreaterThan
 * @see IsGreaterThanOrEqualTo
 * @see IsLessThan
 * @see IsLessThanOrEqualTo
 * @see IsIn
 * @see IsNotIn
 * @see ContainsString
 * @see And
 * @see Not
 * @see Or
 */
@JacocoIgnoreCoverageGenerated
public interface Filter {

    /**
     * Tests if a given object satisfies this {@link Filter}.
     *
     * @param object An object to test.
     * @return {@code true} if a given object satisfies this {@link Filter}, {@code false} otherwise.
     */
    boolean test(Object object);

    /**
     * Creates a new composite filter that represents a logical AND operation between this filter and another filter.
     *
     * @param filter The other filter.
     * @return A new composite filter.
     */
    default Filter and(Filter filter) {
        return and(this, filter);
    }

    /**
     * Creates a new composite filter that represents a logical AND operation between two filters.
     *
     * @param left  The left filter.
     * @param right The right filter.
     * @return A new composite filter.
     */
    static Filter and(Filter left, Filter right) {
        return new And(left, right);
    }

    /**
     * Creates a new composite filter that represents a logical OR operation between this filter and another filter.
     *
     * @param filter The other filter.
     * @return A new composite filter.
     */
    default Filter or(Filter filter) {
        return or(this, filter);
    }

    /**
     * Creates a new composite filter that represents a logical OR operation between two filters.
     *
     * @param left  The left filter.
     * @param right The right filter.
     * @return A new composite filter.
     */
    static Filter or(Filter left, Filter right) {
        return new Or(left, right);
    }

    /**
     * Creates a new composite filter that represents a logical NOT operation on a given filter.
     *
     * @param expression The filter to negate.
     * @return A new composite filter.
     */
    static Filter not(Filter expression) {
        return new Not(expression);
    }
}
