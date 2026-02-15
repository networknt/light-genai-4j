package com.networknt.genai.model.chat.request;

import com.networknt.genai.internal.JacocoIgnoreCoverageGenerated;
import com.networknt.genai.model.chat.request.json.JsonSchema;

import java.util.Objects;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * The response format.
 */
public class ResponseFormat {

    /**
     * Text response format.
     */
    public static final ResponseFormat TEXT = ResponseFormat.builder().type(ResponseFormatType.TEXT).build();
    /**
     * JSON response format.
     */
    public static final ResponseFormat JSON = ResponseFormat.builder().type(ResponseFormatType.JSON).build();

    private final ResponseFormatType type;
    private final JsonSchema jsonSchema;

    private ResponseFormat(Builder builder) {
        this.type = ensureNotNull(builder.type, "type");
        this.jsonSchema = builder.jsonSchema;
        if (jsonSchema != null && type != ResponseFormatType.JSON) {
            throw new IllegalStateException("JsonSchema can be specified only when type=JSON");
        }
    }

    /**
     * Returns the type.
     *
     * @return the type
     */
    public ResponseFormatType type() {
        return type;
    }

    /**
     * Returns the JSON schema.
     *
     * @return the JSON schema
     */
    public JsonSchema jsonSchema() {
        return jsonSchema;
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseFormat that = (ResponseFormat) o;
        return Objects.equals(this.type, that.type)
                && Objects.equals(this.jsonSchema, that.jsonSchema);
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public int hashCode() {
        return Objects.hash(type, jsonSchema);
    }

    @Override
    @JacocoIgnoreCoverageGenerated
    public String toString() {
        return "ResponseFormat {" +
                " type = " + type +
                ", jsonSchema = " + jsonSchema +
                " }";
    }

    /**
     * Creates a new builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link ResponseFormat}.
     */
    public static class Builder {

        private ResponseFormatType type;
        private JsonSchema jsonSchema;

        /**
         * Sets the type.
         *
         * @param type the type
         * @return the builder
         */
        public Builder type(ResponseFormatType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the JSON schema.
         *
         * @param jsonSchema the JSON schema
         * @return the builder
         */
        public Builder jsonSchema(JsonSchema jsonSchema) {
            this.jsonSchema = jsonSchema;
            return this;
        }

        /**
         * Builds the response format.
         *
         * @return the response format
         */
        public ResponseFormat build() {
            return new ResponseFormat(this);
        }
    }
}
