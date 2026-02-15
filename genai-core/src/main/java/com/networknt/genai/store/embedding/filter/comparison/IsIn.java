package com.networknt.genai.store.embedding.filter.comparison;

import com.networknt.genai.data.document.Metadata;
import com.networknt.genai.store.embedding.filter.Filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.networknt.genai.internal.ValidationUtils.ensureNotBlank;
import static com.networknt.genai.internal.ValidationUtils.ensureNotEmpty;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static com.networknt.genai.store.embedding.filter.comparison.NumberComparator.containsAsBigDecimals;
import static com.networknt.genai.store.embedding.filter.comparison.TypeChecker.ensureTypesAreCompatible;
import static com.networknt.genai.store.embedding.filter.comparison.UUIDComparator.containsAsUUID;
import static java.util.Collections.unmodifiableSet;

/**
 * Checks if a metadata value is in a given collection of values.
 */
public class IsIn implements Filter {

    private final String key;
    private final Collection<?> comparisonValues;

    /**
     * Creates a new instance.
     *
     * @param key              the metadata key
     * @param comparisonValues the values to search for
     */
    public IsIn(String key, Collection<?> comparisonValues) {
        this.key = ensureNotBlank(key, "key");
        Set<?> copy = new HashSet<>(ensureNotEmpty(comparisonValues, "comparisonValues with key '" + key + "'"));
        comparisonValues.forEach(value -> ensureNotNull(value, "comparisonValue with key '" + key + "'"));
        this.comparisonValues = unmodifiableSet(copy);
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
     * Returns the comparison values.
     *
     * @return the comparison values
     */
    public Collection<?> comparisonValues() {
        return comparisonValues;
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
        ensureTypesAreCompatible(actualValue, comparisonValues.iterator().next(), key);

        if (comparisonValues.iterator().next() instanceof Number) {
            return containsAsBigDecimals(actualValue, comparisonValues);
        }
        if (comparisonValues.iterator().next() instanceof UUID) {
            return containsAsUUID(actualValue, comparisonValues);
        }

        return comparisonValues.contains(actualValue);
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof IsIn other)) return false;

        return Objects.equals(this.key, other.key)
                && Objects.equals(this.comparisonValues, other.comparisonValues);
    }

    public int hashCode() {
        return Objects.hash(key, comparisonValues);
    }


    public String toString() {
        return "IsIn(key=" + this.key + ", comparisonValues=" + this.comparisonValues + ")";
    }
}
