package com.networknt.genai.store.embedding.filter.logical;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.data.document.Metadata;
import com.networknt.genai.store.embedding.filter.Filter;
import java.util.Objects;

public class Not implements Filter {

    private final Filter expression;

    public Not(Filter expression) {
        this.expression = ensureNotNull(expression, "expression");
    }

    public Filter expression() {
        return expression;
    }

    @Override
    public boolean test(Object object) {
        if (!(object instanceof Metadata)) {
            return false;
        }
        return !expression.test(object);
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Not other)) return false;
        return Objects.equals(this.expression, other.expression);
    }

    public int hashCode() {
        return Objects.hash(expression);
    }

    public String toString() {
        return "Not(expression=" + this.expression + ")";
    }
}
