package com.networknt.agent.service.output;

import com.networknt.agent.Internal;
import com.networknt.agent.model.chat.request.json.JsonArraySchema;
import com.networknt.agent.model.chat.request.json.JsonEnumSchema;
import com.networknt.agent.model.chat.request.json.JsonObjectSchema;
import com.networknt.agent.model.chat.request.json.JsonSchema;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import static com.networknt.agent.internal.ValidationUtils.ensureNotEmpty;
import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;
import static com.networknt.agent.service.output.EnumOutputParser.getEnumDescription;
import static com.networknt.agent.service.output.ParsingUtils.parseAsStringOrJson;
import static java.util.Arrays.stream;

@Internal
abstract class EnumCollectionOutputParser<E extends Enum<E>, CE extends Collection<E>> implements OutputParser<CE> {

    protected final Class<E> enumClass;
    protected final EnumOutputParser<E> enumOutputParser;

    EnumCollectionOutputParser(Class<E> enumClass) {
        this.enumClass = ensureNotNull(enumClass, "enumClass");
        this.enumOutputParser = new EnumOutputParser<>(enumClass);
    }

    @Override
    public CE parse(String text) {
        return parseAsStringOrJson(text, enumOutputParser::parse, emptyCollectionSupplier(), type());
    }

    abstract Supplier<CE> emptyCollectionSupplier();

    private String type() {
        return collectionType() + "<" + enumClass.getName() + ">";
    }

    abstract Class<?> collectionType();

    @Override
    public Optional<JsonSchema> jsonSchema() {
        JsonSchema jsonSchema = JsonSchema.builder()
                .name(collectionType().getSimpleName() + "_of_" + enumClass.getSimpleName())
                .rootElement(JsonObjectSchema.builder()
                        .addProperty("values", JsonArraySchema.builder()
                                .items(JsonEnumSchema.builder()
                                        .enumValues(stream(enumClass.getEnumConstants())
                                                .map(e -> ((Enum<?>) e).name())
                                                .toList())
                                        .build())
                                .build())
                        .required("values")
                        .build())
                .build();
        return Optional.of(jsonSchema);
    }

    @Override
    public String formatInstructions() {
        try {
            E[] enumConstants = enumClass.getEnumConstants();

            ensureNotEmpty(enumConstants, "%s", "Should be at least one enum constant defined.");

            StringBuilder instruction = new StringBuilder();

            // 'enums' keyword will hopefully make it clearer that
            // no description should be included (if present)
            instruction.append("\nYou must answer strictly with zero or more of these enums on a separate line:");

            for (E enumConstant : enumConstants) {
                instruction.append("\n").append(enumConstant.name().toUpperCase(Locale.ROOT));
                Optional<String> optionalEnumDescription = getEnumDescription(enumClass, enumConstant);
                optionalEnumDescription.ifPresent(description -> instruction.append(" - ").append(description));
            }

            return instruction.toString();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
