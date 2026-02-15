package com.networknt.genai.store.embedding.filter.comparison;

import static com.networknt.genai.internal.Exceptions.illegalArgument;
import static com.networknt.genai.internal.ValidationUtils.ensureNotBlank;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.data.document.Metadata;
import com.networknt.genai.store.embedding.filter.Filter;
import java.util.Objects;

/**
 * A filter that checks if the value of a metadata key contains a specific string.
 * The value of the metadata key must be a string.
 */
public class ContainsString implements Filter {

    private final String key;
    private final String comparisonValue;

    /**
     * Creates a new instance.
     *
     * @param key             the metadata key
     * @param comparisonValue the value to search for
     */
    public ContainsString(String key, String comparisonValue) {
        this.key = ensureNotBlank(key, "key");
        this.comparisonValue = ensureNotNull(comparisonValue, "comparisonValue with key '" + key + "'");
    }

    /**
     * Returns the metadata key.
     *
     * @return the metadata key
     */
    public String key() {
        return key;
    }

    /**
     * Returns the comparison value.
     *
     * @return the comparison value
     */
    public String comparisonValue() {
        return comparisonValue;
    }

    @Override
    public boolean test(Object object) {
        if (!(object instanceof Metadata metadata)) {
            return false;
        }

        if (!metadata.containsKey(key)) {
            return false;
        }

        Object actualValue = metadata.toMap().get(key);

        if (actualValue instanceof String str) {
            return str.contains(comparisonValue);
        }

        throw illegalArgument(
                "Type mismatch: actual value of metadata key \"%s\" (%s) has type %s, "
                        + "while it is expected to be a string",
                key, actualValue, actualValue.getClass().getName());
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ContainsString other)) return false;

        return Objects.equals(this.key, other.key) && Objects.equals(this.comparisonValue, other.comparisonValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, comparisonValue);
    }

    @Override
    public String toString() {
        return "ContainsString(key=" + this.key + ", comparisonValue=" + this.comparisonValue + ")";
    }
}
