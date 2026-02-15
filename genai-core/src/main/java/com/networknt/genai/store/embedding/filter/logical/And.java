package com.networknt.genai.store.embedding.filter.logical;

import com.networknt.genai.store.embedding.filter.Filter;

import java.util.Objects;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * A logical AND filter.
 */
public class And implements Filter {

    private final Filter left;
    private final Filter right;

    /**
     * Creates a new AND filter.
     *
     * @param left the left filter
     * @param right the right filter
     */
    public And(Filter left, Filter right) {
        this.left = ensureNotNull(left, "left");
        this.right = ensureNotNull(right, "right");
    }

    /**
     * @return the left filter
     */
    public Filter left() {
        return left;
    }

    /**
     * @return the right filter
     */
    public Filter right() {
        return right;
    }

    @Override
    public boolean test(Object object) {
        return left().test(object) && right().test(object);
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof And other)) return false;
        return Objects.equals(this.left, other.left) && Objects.equals(this.right, other.right);
    }

    public int hashCode() {
        return Objects.hash(left, right);
    }

    public String toString() {
        return "And(left=" + this.left + ", right=" + this.right + ")";
    }
}
