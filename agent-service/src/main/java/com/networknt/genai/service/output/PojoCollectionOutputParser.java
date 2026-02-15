package com.networknt.genai.service.output;

import com.networknt.genai.Internal;
import com.networknt.genai.model.chat.request.json.JsonArraySchema;
import com.networknt.genai.model.chat.request.json.JsonObjectSchema;
import com.networknt.genai.model.chat.request.json.JsonSchema;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Supplier;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static com.networknt.genai.internal.JsonSchemaElementUtils.jsonObjectOrReferenceSchemaFrom;
import static com.networknt.genai.service.output.ParsingUtils.parseAsStringOrJson;

@Internal
abstract class PojoCollectionOutputParser<T, CT extends Collection<T>> implements OutputParser<CT> {

    private final Class<T> type;
    private final PojoOutputParser<T> parser;

    PojoCollectionOutputParser(Class<T> type) {
        this.type = ensureNotNull(type, "type");
        this.parser = new PojoOutputParser<>(type);
    }

    @Override
    public CT parse(String text) {
        return parseAsStringOrJson(text, parser::parse, emptyCollectionSupplier(), type());
    }

    abstract Supplier<CT> emptyCollectionSupplier();

    private String type() {
        return collectionType().getName() + "<" + type.getName() + ">";
    }

    abstract Class<?> collectionType();

    @Override
    public Optional<JsonSchema> jsonSchema() {
        JsonSchema jsonSchema = JsonSchema.builder()
                .name(collectionType().getSimpleName() + "_of_" + type.getSimpleName())
                .rootElement(JsonObjectSchema.builder()
                        .addProperty("values", JsonArraySchema.builder()
                                .items(jsonObjectOrReferenceSchemaFrom(type, null, false, new LinkedHashMap<>(), true))
                                .build())
                        .required("values")
                        .build())
                .build();
        return Optional.of(jsonSchema);
    }

    @Override
    public String formatInstructions() {
        throw new IllegalStateException();
    }
}
