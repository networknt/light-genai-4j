package com.networknt.agent.store.embedding.filter.comparison;

import com.networknt.agent.data.document.Metadata;
import com.networknt.agent.store.embedding.filter.Filter;

import java.util.Objects;

import static com.networknt.agent.internal.ValidationUtils.ensureNotBlank;
import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;
import static com.networknt.agent.store.embedding.filter.comparison.NumberComparator.compareAsBigDecimals;
import static com.networknt.agent.store.embedding.filter.comparison.TypeChecker.ensureTypesAreCompatible;

public class IsGreaterThan implements Filter {

    private final String key;
    private final Comparable<?> comparisonValue;

    public IsGreaterThan(String key, Comparable<?> comparisonValue) {
        this.key = ensureNotBlank(key, "key");
        this.comparisonValue = ensureNotNull(comparisonValue, "comparisonValue with key '" + key + "'");
    }

    public String key() {
        return key;
    }

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
            return compareAsBigDecimals(actualValue, comparisonValue) > 0;
        }

        return ((Comparable) actualValue).compareTo(comparisonValue) > 0;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof IsGreaterThan other)) return false;

        return Objects.equals(this.key, other.key)
                && Objects.equals(this.comparisonValue, other.comparisonValue);
    }

    public int hashCode() {
        return Objects.hash(key, comparisonValue);
    }

    public String toString() {
        return "IsGreaterThan(key=" + this.key + ", comparisonValue=" + this.comparisonValue + ")";
    }
}
