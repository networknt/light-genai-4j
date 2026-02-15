package com.networknt.genai.model.chat.request.json;

import static com.networknt.genai.internal.Utils.quoted;

import java.util.Objects;

/**
 * Represents a JSON schema "array" element.
 */
public class JsonArraySchema implements JsonSchemaElement {

    private final String description;
    private final JsonSchemaElement items;

    /**
     * Creates a new JSON array schema.
     *
     * @param builder the builder
     */
    public JsonArraySchema(Builder builder) {
        this.description = builder.description;
        this.items = builder.items;
    }

    @Override
    public String description() {
        return description;
    }

    /**
     * Returns the items schema.
     *
     * @return the items schema
     */
    public JsonSchemaElement items() {
        return items;
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
     * Builder for {@link JsonArraySchema}.
     */
    public static class Builder {

        private String description;
        private JsonSchemaElement items;

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
         * Sets the items schema.
         *
         * @param items the items schema
         * @return the builder
         */
        public Builder items(JsonSchemaElement items) {
            this.items = items;
            return this;
        }

        /**
         * Builds the JSON array schema.
         *
         * @return the JSON array schema
         */
        public JsonArraySchema build() {
            return new JsonArraySchema(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonArraySchema that = (JsonArraySchema) o;
        return Objects.equals(this.description, that.description) && Objects.equals(this.items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, items);
    }

    @Override
    public String toString() {
        return "JsonArraySchema {" + "description = " + quoted(description) + ", items = " + items + " }";
    }
}
