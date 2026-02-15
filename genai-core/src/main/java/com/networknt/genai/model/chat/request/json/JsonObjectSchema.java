package com.networknt.genai.model.chat.request.json;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.Utils.quoted;
import static java.util.Arrays.asList;

import java.util.*;

/**
 * Represents a JSON schema "object" element.
 */
public class JsonObjectSchema implements JsonSchemaElement {

    private final String description;
    private final Map<String, JsonSchemaElement> properties;
    private final List<String> required;
    private final Boolean additionalProperties;
    private final Map<String, JsonSchemaElement> definitions;

    /**
     * Creates a new JSON object schema.
     *
     * @param builder the builder
     */
    public JsonObjectSchema(Builder builder) {
        this.description = builder.description;
        this.properties = copy(builder.properties);
        this.required = copy(builder.required);
        this.additionalProperties = builder.additionalProperties;
        this.definitions = copy(builder.definitions);
    }

    @Override
    public String description() {
        return description;
    }

    /**
     * Returns the properties.
     *
     * @return the properties
     */
    public Map<String, JsonSchemaElement> properties() {
        return properties;
    }

    /**
     * Returns the list of required property names.
     *
     * @return the list of required property names
     */
    public List<String> required() {
        return required;
    }

    /**
     * Returns whether additional properties are allowed.
     *
     * @return whether additional properties are allowed
     */
    public Boolean additionalProperties() {
        return additionalProperties;
    }

    /**
     * Used together with {@link JsonReferenceSchema} when recursion is required.
     *
     * @return the definitions
     */
    public Map<String, JsonSchemaElement> definitions() {
        return definitions;
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
     * Returns a new builder initialized with the current values.
     *
     * @return a new builder
     */
    public Builder toBuilder() {
        return builder()
                .description(this.description)
                .addProperties(this.properties)
                .required(this.required != null ? new ArrayList<>(this.required) : null)
                .additionalProperties(this.additionalProperties)
                .definitions(this.definitions != null ? new LinkedHashMap<>(this.definitions) : null);
    }

    /**
     * Builder for {@link JsonObjectSchema}.
     */
    public static class Builder {

        private String description;
        private final Map<String, JsonSchemaElement> properties = new LinkedHashMap<>();
        private List<String> required;
        private Boolean additionalProperties;
        private Map<String, JsonSchemaElement> definitions;

        /**
         * Default constructor.
         */
        public Builder() {}

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * Adds all properties in the parameter Map to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param properties the properties to add
         * @return the builder
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addStringProperty(String)
         * @see #addStringProperty(String, String)
         * @see #addIntegerProperty(String)
         * @see #addIntegerProperty(String, String)
         * @see #addNumberProperty(String)
         * @see #addNumberProperty(String, String)
         * @see #addBooleanProperty(String)
         * @see #addBooleanProperty(String, String)
         * @see #addEnumProperty(String, List)
         * @see #addEnumProperty(String, List, String)
         */
        public Builder addProperties(Map<String, JsonSchemaElement> properties) {
            this.properties.putAll(properties);
            return this;
        }

        /**
         * Adds a single property to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @param jsonSchemaElement the property schema
         * @return the builder
         * @see #addProperties(Map)
         * @see #addStringProperty(String)
         * @see #addStringProperty(String, String)
         * @see #addIntegerProperty(String)
         * @see #addIntegerProperty(String, String)
         * @see #addNumberProperty(String)
         * @see #addNumberProperty(String, String)
         * @see #addBooleanProperty(String)
         * @see #addBooleanProperty(String, String)
         * @see #addEnumProperty(String, List)
         * @see #addEnumProperty(String, List, String)
         */
        public Builder addProperty(String name, JsonSchemaElement jsonSchemaElement) {
            this.properties.put(name, jsonSchemaElement);
            return this;
        }

        /**
         * Adds a single string property to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @return the builder
         * @see #addStringProperty(String, String)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addStringProperty(String name) {
            this.properties.put(name, new JsonStringSchema());
            return this;
        }

        /**
         * Adds a single string property with a description to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @param description the property description
         * @return the builder
         * @see #addStringProperty(String)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addStringProperty(String name, String description) {
            this.properties.put(
                    name, JsonStringSchema.builder().description(description).build());
            return this;
        }

        /**
         * Adds a single integer property to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @return the builder
         * @see #addIntegerProperty(String, String)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addIntegerProperty(String name) {
            this.properties.put(name, new JsonIntegerSchema());
            return this;
        }

        /**
         * Adds a single integer property with a description to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @param description the property description
         * @return the builder
         * @see #addIntegerProperty(String)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addIntegerProperty(String name, String description) {
            this.properties.put(
                    name, JsonIntegerSchema.builder().description(description).build());
            return this;
        }

        /**
         * Adds a single number property to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @return the builder
         * @see #addNumberProperty(String, String)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addNumberProperty(String name) {
            this.properties.put(name, new JsonNumberSchema());
            return this;
        }

        /**
         * Adds a single number property with a description to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @param description the property description
         * @return the builder
         * @see #addNumberProperty(String)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addNumberProperty(String name, String description) {
            this.properties.put(
                    name, JsonNumberSchema.builder().description(description).build());
            return this;
        }

        /**
         * Adds a single boolean property to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @return the builder
         * @see #addBooleanProperty(String, String)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addBooleanProperty(String name) {
            this.properties.put(name, new JsonBooleanSchema());
            return this;
        }

        /**
         * Adds a single boolean property with a description to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @param description the property description
         * @return the builder
         * @see #addBooleanProperty(String)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addBooleanProperty(String name, String description) {
            this.properties.put(
                    name, JsonBooleanSchema.builder().description(description).build());
            return this;
        }

        /**
         * Adds a single enum property to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @param enumValues the enum values
         * @return the builder
         * @see #addEnumProperty(String, List, String)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addEnumProperty(String name, List<String> enumValues) {
            this.properties.put(
                    name, JsonEnumSchema.builder().enumValues(enumValues).build());
            return this;
        }

        /**
         * Adds a single enum property with a description to the properties of this JSON object.
         * Please note that {@link #required(List)} should be set explicitly if you want the properties to be mandatory.
         *
         * @param name the property name
         * @param enumValues the enum values
         * @param description the property description
         * @return the builder
         * @see #addEnumProperty(String, List)
         * @see #addProperty(String, JsonSchemaElement)
         * @see #addProperties(Map)
         */
        public Builder addEnumProperty(String name, List<String> enumValues, String description) {
            this.properties.put(
                    name,
                    JsonEnumSchema.builder()
                            .enumValues(enumValues)
                            .description(description)
                            .build());
            return this;
        }

        /**
         * Sets the required property names.
         *
         * @param required the list of required property names
         * @return the builder
         */
        public Builder required(List<String> required) {
            this.required = required;
            return this;
        }

        /**
         * Sets the required property names.
         *
         * @param required the required property names
         * @return the builder
         */
        public Builder required(String... required) {
            return required(asList(required));
        }

        /**
         * Sets whether additional properties are allowed.
         *
         * @param additionalProperties whether additional properties are allowed
         * @return the builder
         */
        public Builder additionalProperties(Boolean additionalProperties) {
            this.additionalProperties = additionalProperties;
            return this;
        }

        /**
         * Used together with {@link JsonReferenceSchema} when recursion is required.
         *
         * @param definitions the definitions
         * @return the builder
         */
        public Builder definitions(Map<String, JsonSchemaElement> definitions) {
            this.definitions = definitions;
            return this;
        }

        /**
         * Builds the JSON object schema.
         *
         * @return the JSON object schema
         */
        public JsonObjectSchema build() {
            return new JsonObjectSchema(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonObjectSchema that = (JsonObjectSchema) o;
        return Objects.equals(this.description, that.description)
                && Objects.equals(this.properties, that.properties)
                && Objects.equals(this.required, that.required)
                && Objects.equals(this.additionalProperties, that.additionalProperties)
                && Objects.equals(this.definitions, that.definitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, properties, required, additionalProperties, definitions);
    }

    @Override
    public String toString() {
        return "JsonObjectSchema {" + "description = "
                + quoted(description) + ", properties = "
                + properties + ", required = "
                + required + ", additionalProperties = "
                + additionalProperties + ", definitions = "
                + definitions + " }";
    }
}
