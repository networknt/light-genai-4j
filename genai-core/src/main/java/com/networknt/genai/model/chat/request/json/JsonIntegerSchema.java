package com.networknt.genai.model.chat.request.json;

import java.util.Objects;

import static com.networknt.genai.internal.Utils.quoted;

/**
 * Represents a JSON schema "integer" element.
 */
public class JsonIntegerSchema implements JsonSchemaElement {

    private final String description;

    /**
     * Creates a new JSON integer schema.
     */
    public JsonIntegerSchema() {
        this.description = null;
    }

    /**
     * Creates a new JSON integer schema.
     *
     * @param builder the builder
     */
    public JsonIntegerSchema(Builder builder) {
        this.description = builder.description;
    }

    @Override
    public String description() {
        return description;
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
     * Builder for {@link JsonIntegerSchema}.
     */
    public static class Builder {

        private String description;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Sets the description.
         *
         * @param description the description
         * @return the builder
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Builds the JSON integer schema.
         *
         * @return the JSON integer schema
         */
        public JsonIntegerSchema build() {
            return new JsonIntegerSchema(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonIntegerSchema that = (JsonIntegerSchema) o;
        return Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return "JsonIntegerSchema {" +
                "description = " + quoted(description) +
                " }";
    }
}
