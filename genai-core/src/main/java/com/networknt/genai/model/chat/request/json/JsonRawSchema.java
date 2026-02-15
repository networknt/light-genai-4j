package com.networknt.genai.model.chat.request.json;

import static com.networknt.genai.internal.Utils.quoted;
import static com.networknt.genai.internal.ValidationUtils.ensureNotBlank;

import java.util.Objects;

/**
 * Represents a raw JSON schema element.
 */
public class JsonRawSchema implements JsonSchemaElement {

    private final String schema;

    /**
     * Creates a new JSON raw schema.
     *
     * @param builder the builder
     */
    public JsonRawSchema(Builder builder) {
        this.schema = ensureNotBlank(builder.schema, "schema");
    }

    @Override
    public String description() {
        return null;
    }

    /**
     * Returns the raw schema.
     *
     * @return the raw schema
     */
    public String schema() {
        return schema;
    }

    /**
     * Creates a new JSON raw schema from a string.
     *
     * @param schema the raw schema
     * @return the JSON raw schema
     */
    public static JsonRawSchema from(String schema) {
        return builder().schema(schema).build();
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
     * Builder for {@link JsonRawSchema}.
     */
    public static class Builder {

        /**
         * The raw schema.
         */
        public String schema;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Sets the raw schema.
         *
         * @param schema the raw schema
         * @return the builder
         */
        public Builder schema(String schema) {
            this.schema = schema;
            return this;
        }

        /**
         * Builds the JSON raw schema.
         *
         * @return the JSON raw schema
         */
        public JsonRawSchema build() {
            return new JsonRawSchema(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JsonRawSchema that = (JsonRawSchema) o;
        return Objects.equals(this.schema, that.schema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schema);
    }

    @Override
    public String toString() {
        return "JsonRawSchema {" + "schema = " + quoted(schema) + " }";
    }
}
