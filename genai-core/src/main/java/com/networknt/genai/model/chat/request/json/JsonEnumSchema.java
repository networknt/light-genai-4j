package com.networknt.genai.model.chat.request.json;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.Utils.quoted;
import static com.networknt.genai.internal.ValidationUtils.ensureNotEmpty;

import java.util.List;
import java.util.Objects;

/**
 * Represents a JSON schema "enum" element.
 */
public class JsonEnumSchema implements JsonSchemaElement {

    private final String description;
    private final List<String> enumValues;

    /**
     * Creates a new JSON enum schema.
     *
     * @param builder the builder
     */
    public JsonEnumSchema(Builder builder) {
        this.description = builder.description;
        this.enumValues = copy(ensureNotEmpty(builder.enumValues, "enumValues"));
    }

    @Override
    public String description() {
        return description;
    }

    /**
     * Returns the enum values.
     *
     * @return the enum values
     */
    public List<String> enumValues() {
        return enumValues;
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
     * Builder for {@link JsonEnumSchema}.
     */
    public static class Builder {

        private String description;
        private List<String> enumValues;

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
         * Sets the enum values.
         *
         * @param enumValues the enum values
         * @return the builder
         */
        public Builder enumValues(List<String> enumValues) {
            this.enumValues = enumValues;
            return this;
        }

        /**
         * Sets the enum values.
         *
         * @param enumValues the enum values
         * @return the builder
         */
        public Builder enumValues(String... enumValues) {
            return enumValues(List.of(enumValues));
        }

        /**
         * Builds the JSON enum schema.
         *
         * @return the JSON enum schema
         */
        public JsonEnumSchema build() {
            return new JsonEnumSchema(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonEnumSchema that = (JsonEnumSchema) o;
        return Objects.equals(this.description, that.description) && Objects.equals(this.enumValues, that.enumValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, enumValues);
    }

    @Override
    public String toString() {
        return "JsonEnumSchema {" + "description = " + quoted(description) + ", enumValues = " + enumValues + " }";
    }
}
