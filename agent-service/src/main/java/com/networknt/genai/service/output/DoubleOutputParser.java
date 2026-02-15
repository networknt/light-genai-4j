package com.networknt.genai.service.output;

import com.networknt.genai.Internal;
import com.networknt.genai.model.chat.request.json.JsonObjectSchema;
import com.networknt.genai.model.chat.request.json.JsonSchema;

import java.util.Optional;

import static com.networknt.genai.service.output.ParsingUtils.parseAsStringOrJson;

@Internal
class DoubleOutputParser implements OutputParser<Double> {

    @Override
    public Double parse(String text) {
        return parseAsStringOrJson(text, Double::parseDouble, Double.class);
    }

    @Override
    public Optional<JsonSchema> jsonSchema() {
        JsonSchema jsonSchema = JsonSchema.builder()
                .name("number")
                .rootElement(JsonObjectSchema.builder()
                        .addNumberProperty("value")
                        .required("value")
                        .build())
                .build();
        return Optional.of(jsonSchema);
    }

    @Override
    public String formatInstructions() {
        return "floating point number";
    }
}
