package com.networknt.genai.model.chat.request.json;

import java.util.Objects;

import static com.networknt.genai.internal.Utils.quoted;

/**
 * Represents a JSON schema "string" element.
 */
public class JsonStringSchema implements JsonSchemaElement {

    private final String description;

    /**
     * Creates a new JSON string schema.
     */
    public JsonStringSchema() {
        this.description = null;
    }

    /**
     * Creates a new JSON string schema.
     *
     * @param builder the builder
     */
    public JsonStringSchema(Builder builder) {
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
     * Builder for {@link JsonStringSchema}.
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
         * Builds the JSON string schema.
         *
         * @return the JSON string schema
         */
        public JsonStringSchema build() {
            return new JsonStringSchema(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonStringSchema that = (JsonStringSchema) o;
        return Objects.equals(this.description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return "JsonStringSchema {" +
                "description = " + quoted(description) +
                " }";
    }
}
