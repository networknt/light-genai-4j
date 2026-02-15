package com.networknt.genai.model.chat.request.json;

import java.util.Objects;

import static com.networknt.genai.internal.Utils.quoted;

/**
 * Can reference {@link JsonObjectSchema} when recursion is required.
 * When used, the {@link JsonObjectSchema#definitions()} of the root JSON schema element
 * should contain an entry with a key equal to the {@link #reference()} of this {@link JsonReferenceSchema}.
 */
public class JsonReferenceSchema implements JsonSchemaElement {

    private final String reference;

    /**
     * Creates a new JSON reference schema.
     *
     * @param builder the builder
     */
    public JsonReferenceSchema(Builder builder) {
        this.reference = builder.reference;
    }

    /**
     * Returns the reference.
     *
     * @return the reference
     */
    public String reference() {
        return reference;
    }

    @Override
    public String description() {
        return null;
    }

    /**
     * Returns a new builder.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link JsonReferenceSchema}.
     */
    public static class Builder {

        private String reference;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Sets the reference.
         *
         * @param reference the reference
         * @return the builder
         */
        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }

        /**
         * Builds the JSON reference schema.
         *
         * @return the JSON reference schema
         */
        public JsonReferenceSchema build() {
            return new JsonReferenceSchema(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonReferenceSchema that = (JsonReferenceSchema) o;
        return Objects.equals(this.reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }

    @Override
    public String toString() {
        return "JsonReferenceSchema {" +
                "reference = " + quoted(reference) +
                " }";
    }
}
