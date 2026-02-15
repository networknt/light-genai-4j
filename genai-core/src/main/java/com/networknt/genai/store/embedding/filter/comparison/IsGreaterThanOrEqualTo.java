package com.networknt.genai.store.embedding.filter.comparison;

import com.networknt.genai.data.document.Metadata;
import com.networknt.genai.store.embedding.filter.Filter;

import java.util.Objects;

import static com.networknt.genai.internal.ValidationUtils.ensureNotBlank;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static com.networknt.genai.store.embedding.filter.comparison.NumberComparator.compareAsBigDecimals;
import static com.networknt.genai.store.embedding.filter.comparison.TypeChecker.ensureTypesAreCompatible;

/**
 * Checks if a metadata value is greater than or equal to a given value.
 */
public class IsGreaterThanOrEqualTo implements Filter {

    private final String key;
    private final Comparable<?> comparisonValue;

    /**
     * Creates a new instance.
     *
     * @param key             the metadata key
     * @param comparisonValue the value to compare with
     */
    public IsGreaterThanOrEqualTo(String key, Comparable<?> comparisonValue) {
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
    public Comparable<?> comparisonValue() {
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
        ensureTypesAreCompatible(actualValue, comparisonValue, key);

        if (actualValue instanceof Number) {
            return compareAsBigDecimals(actualValue, comparisonValue) >= 0;
        }

        return ((Comparable) actualValue).compareTo(comparisonValue) >= 0;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof IsGreaterThanOrEqualTo other)) return false;

        return Objects.equals(this.key, other.key)
                && Objects.equals(this.comparisonValue, other.comparisonValue);
    }

    public int hashCode() {
        return Objects.hash(key, comparisonValue);
    }

    public String toString() {
        return "IsGreaterThanOrEqualTo(key=" + this.key + ", comparisonValue=" + this.comparisonValue + ")";
    }
}
