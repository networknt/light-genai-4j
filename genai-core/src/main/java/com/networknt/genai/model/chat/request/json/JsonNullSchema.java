package com.networknt.genai.model.chat.request.json;

/**
 * Represents a JSON schema "null" element.
 */
public class JsonNullSchema implements JsonSchemaElement {

    /**
     * Default constructor.
     */
    public JsonNullSchema() {}

    @Override
    public String description() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return JsonNullSchema.class.hashCode();
    }

    @Override
    public String toString() {
        return "JsonNullSchema {}";
    }
}
