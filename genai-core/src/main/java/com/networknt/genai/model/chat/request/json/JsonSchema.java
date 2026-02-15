package com.networknt.genai.model.chat.request.json;

import java.util.Objects;

import static com.networknt.genai.internal.Utils.quoted;

/**
 * Represents a JSON schema.
 */
public class JsonSchema {

    private final String name;
    private final JsonSchemaElement rootElement;

    /**
     * Creates a new JSON schema.
     *
     * @param builder the builder
     */
    private JsonSchema(Builder builder) {
        this.name = builder.name;
        this.rootElement = builder.rootElement;
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the root element.
     *
     * @return the root element
     */
    public JsonSchemaElement rootElement() {
        return rootElement;
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
     * Builder for {@link JsonSchema}.
     */
    public static class Builder {

        private String name;
        private JsonSchemaElement rootElement;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Sets the name.
         *
         * @param name the name
         * @return the builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the root element.
         *
         * @param rootElement the root element
         * @return the builder
         */
        public Builder rootElement(JsonSchemaElement rootElement) {
            this.rootElement = rootElement;
            return this;
        }

        /**
         * Builds the JSON schema.
         *
         * @return the JSON schema
         */
        public JsonSchema build() {
            return new JsonSchema(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonSchema that = (JsonSchema) o;
        return Objects.equals(this.name, that.name)
                && Objects.equals(this.rootElement, that.rootElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rootElement);
    }

    @Override
    public String toString() {
        return "JsonSchema {" +
                " name = " + quoted(name) +
                ", rootElement = " + rootElement +
                " }";
    }
}
