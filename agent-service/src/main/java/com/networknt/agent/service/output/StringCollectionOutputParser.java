package com.networknt.agent.service.output;

import com.networknt.agent.Internal;
import com.networknt.agent.model.chat.request.json.JsonArraySchema;
import com.networknt.agent.model.chat.request.json.JsonObjectSchema;
import com.networknt.agent.model.chat.request.json.JsonSchema;
import com.networknt.agent.model.chat.request.json.JsonStringSchema;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

import static com.networknt.agent.service.output.ParsingUtils.parseAsStringOrJson;

@Internal
abstract class StringCollectionOutputParser<CT extends Collection<String>> implements OutputParser<CT> {

    @Override
    public CT parse(String text) {
        return parseAsStringOrJson(text, s -> s, emptyCollectionSupplier(), type());
    }

    abstract Supplier<CT> emptyCollectionSupplier();

    private String type() {
        return collectionType().getName() + "<java.lang.String>";
    }

    abstract Class<?> collectionType();

    @Override
    public Optional<JsonSchema> jsonSchema() {
        JsonSchema jsonSchema = JsonSchema.builder()
                .name(collectionType().getSimpleName() + "_of_String")
                .rootElement(JsonObjectSchema.builder()
                        .addProperty("values", JsonArraySchema.builder()
                                .items(new JsonStringSchema())
                                .build())
                        .required("values")
                        .build())
                .build();
        return Optional.of(jsonSchema);
    }

    @Override
    public String formatInstructions() {
        return "\nYou must put every item on a separate line.";
    }
}
