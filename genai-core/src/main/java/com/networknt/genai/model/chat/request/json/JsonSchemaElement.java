package com.networknt.genai.model.chat.request.json;

/**
 * A base interface for a JSON schema element.
 *
 * @see JsonAnyOfSchema
 * @see JsonArraySchema
 * @see JsonBooleanSchema
 * @see JsonEnumSchema
 * @see JsonIntegerSchema
 * @see JsonNullSchema
 * @see JsonNumberSchema
 * @see JsonObjectSchema
 * @see JsonRawSchema
 * @see JsonReferenceSchema
 * @see JsonStringSchema
 */
/**
 * Represents an element of a JSON schema.
 */
public interface JsonSchemaElement {

    /**
     * Returns the description of the element.
     *
     * @return the description of the element
     */
    String description();
}
