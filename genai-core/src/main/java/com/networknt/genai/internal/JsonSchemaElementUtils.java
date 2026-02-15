package com.networknt.genai.internal;

import static com.networknt.genai.internal.Utils.generateUUIDFrom;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.networknt.genai.Internal;
import com.networknt.genai.model.chat.request.json.JsonAnyOfSchema;
import com.networknt.genai.model.chat.request.json.JsonArraySchema;
import com.networknt.genai.model.chat.request.json.JsonBooleanSchema;
import com.networknt.genai.model.chat.request.json.JsonEnumSchema;
import com.networknt.genai.model.chat.request.json.JsonIntegerSchema;
import com.networknt.genai.model.chat.request.json.JsonNullSchema;
import com.networknt.genai.model.chat.request.json.JsonNumberSchema;
import com.networknt.genai.model.chat.request.json.JsonObjectSchema;
import com.networknt.genai.model.chat.request.json.JsonRawSchema;
import com.networknt.genai.model.chat.request.json.JsonReferenceSchema;
import com.networknt.genai.model.chat.request.json.JsonSchemaElement;
import com.networknt.genai.model.chat.request.json.JsonStringSchema;
import com.networknt.genai.model.output.structured.Description;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Internal
public class JsonSchemaElementUtils {

    private static final String DEFAULT_UUID_DESCRIPTION = "String in a UUID format";

    /**
     * Creates a {@link JsonSchemaElement} from a class.
     *
     * @param clazz the class
     * @return the JSON schema element
     */
    public static JsonSchemaElement jsonSchemaElementFrom(Class<?> clazz) {
        return jsonSchemaElementFrom(clazz, clazz, null, false, new LinkedHashMap<>());
    }

    /**
     * Creates a {@link JsonSchemaElement} from a class and type.
     *
     * @param clazz the class
     * @param type the type
     * @param fieldDescription the field description
     * @param areSubFieldsRequiredByDefault whether subfields are required by default
     * @param visited the map of visited classes
     * @return the JSON schema element
     */
    public static JsonSchemaElement jsonSchemaElementFrom(
            Class<?> clazz,
            Type type,
            String fieldDescription,
            boolean areSubFieldsRequiredByDefault,
            Map<Class<?>, VisitedClassMetadata> visited) {
        if (isJsonString(clazz)) {
            return JsonStringSchema.builder()
                    .description(Optional.ofNullable(fieldDescription).orElse(descriptionFrom(clazz)))
                    .build();
        }

        if (isJsonInteger(clazz)) {
            return JsonIntegerSchema.builder().description(fieldDescription).build();
        }

        if (isJsonNumber(clazz)) {
            return JsonNumberSchema.builder().description(fieldDescription).build();
        }

        if (isJsonBoolean(clazz)) {
            return JsonBooleanSchema.builder().description(fieldDescription).build();
        }

        if (clazz.isEnum()) {
            return JsonEnumSchema.builder()
                    .enumValues(stream(clazz.getEnumConstants())
                            .map(e -> ((Enum<?>) e).name())
                            .toList())
                    .description(Optional.ofNullable(fieldDescription).orElse(descriptionFrom(clazz)))
                    .build();
        }

        if (clazz.isArray()) {
            return JsonArraySchema.builder()
                    .items(jsonSchemaElementFrom(
                            clazz.getComponentType(), null, null, areSubFieldsRequiredByDefault, visited))
                    .description(fieldDescription)
                    .build();
        }

        if (Collection.class.isAssignableFrom(clazz)) {
            return JsonArraySchema.builder()
                    .items(jsonSchemaElementFrom(
                            getActualType(type), null, null, areSubFieldsRequiredByDefault, visited))
                    .description(fieldDescription)
                    .build();
        }

        return jsonObjectOrReferenceSchemaFrom(clazz, fieldDescription, areSubFieldsRequiredByDefault, visited, false);
    }

    /**
     * Creates a {@link JsonObjectSchema} or {@link JsonReferenceSchema} from a type.
     *
     * @param type the type
     * @param description the description
     * @param areSubFieldsRequiredByDefault whether subfields are required by default
     * @param visited the map of visited classes
     * @param setDefinitions whether to set definitions
     * @return the JSON schema element
     */
    public static JsonSchemaElement jsonObjectOrReferenceSchemaFrom(
            Class<?> type,
            String description,
            boolean areSubFieldsRequiredByDefault,
            Map<Class<?>, VisitedClassMetadata> visited,
            boolean setDefinitions) {
        if (visited.containsKey(type) && isCustomClass(type)) {
            VisitedClassMetadata visitedClassMetadata = visited.get(type);
            JsonSchemaElement jsonSchemaElement = visitedClassMetadata.jsonSchemaElement;
            if (jsonSchemaElement instanceof JsonReferenceSchema) {
                visitedClassMetadata.recursionDetected = true;
            }
            if (jsonSchemaElement instanceof JsonObjectSchema obj) {
                if (Objects.equals(description, obj.description())) {
                    return obj;
                }
                return obj.toBuilder().description(description).build();
            }

            return jsonSchemaElement;
        }

        String reference = generateUUIDFrom(type.getName());
        JsonReferenceSchema jsonReferenceSchema =
                JsonReferenceSchema.builder().reference(reference).build();
        visited.put(type, new VisitedClassMetadata(jsonReferenceSchema, reference, false));

        Map<String, JsonSchemaElement> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            String fieldName = field.getName();
            if (isStatic(field.getModifiers()) || fieldName.equals("__$hits$__") || fieldName.startsWith("this$")) {
                continue;
            }
            if (isRequired(field, areSubFieldsRequiredByDefault)) {
                required.add(fieldName);
            }
            String fieldDescription = descriptionFrom(field);
            JsonSchemaElement jsonSchemaElement = jsonSchemaElementFrom(
                    field.getType(), field.getGenericType(), fieldDescription, areSubFieldsRequiredByDefault, visited);
            properties.put(fieldName, jsonSchemaElement);
        }

        JsonObjectSchema.Builder builder = JsonObjectSchema.builder()
                .description(Optional.ofNullable(description).orElse(descriptionFrom(type)))
                .addProperties(properties)
                .required(required);

        visited.get(type).jsonSchemaElement = builder.build();

        if (setDefinitions) {
            Map<String, JsonSchemaElement> definitions = new LinkedHashMap<>();
            visited.forEach((clazz, visitedClassMetadata) -> {
                if (visitedClassMetadata.recursionDetected) {
                    definitions.put(visitedClassMetadata.reference, visitedClassMetadata.jsonSchemaElement);
                }
            });
            if (!definitions.isEmpty()) {
                builder.definitions(definitions);
            }
        }

        return builder.build();
    }

    private static boolean isRequired(Field field, boolean defaultValue) {
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (jsonProperty != null) {
            return jsonProperty.required();
        }

        return defaultValue;
    }

    private static String descriptionFrom(Field field) {
        return descriptionFrom(field.getAnnotation(Description.class));
    }

    private static String descriptionFrom(Class<?> type) {
        if (type == UUID.class) {
            return DEFAULT_UUID_DESCRIPTION;
        }
        return descriptionFrom(type.getAnnotation(Description.class));
    }

    private static String descriptionFrom(Description description) {
        if (description == null) {
            return null;
        }
        return String.join(" ", description.value());
    }

    private static Class<?> getActualType(Type type) {
        if (type instanceof final ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                return (Class<?>) actualTypeArguments[0];
            }
        }
        return null;
    }

    static boolean isCustomClass(Class<?> clazz) {
        if (clazz.getPackage() != null) {
            String packageName = clazz.getPackage().getName();
            if (packageName.startsWith("java.")
                    || packageName.startsWith("javax.")
                    || packageName.startsWith("jdk.")
                    || packageName.startsWith("sun.")
                    || packageName.startsWith("com.sun.")) {
                return false;
            }
        }

        return true;
    }

    /**
     * Converts a map of properties to a map of maps.
     *
     * @param properties the properties
     * @return the map of maps
     */
    public static Map<String, Map<String, Object>> toMap(Map<String, JsonSchemaElement> properties) {
        return toMap(properties, false);
    }

    /**
     * Converts a map of properties to a map of maps.
     *
     * @param properties the properties
     * @param strict whether to use strict mode
     * @return the map of maps
     */
    public static Map<String, Map<String, Object>> toMap(Map<String, JsonSchemaElement> properties, boolean strict) {
        Map<String, Map<String, Object>> map = new LinkedHashMap<>();
        properties.forEach((property, value) -> map.put(property, toMap(value, strict)));
        return map;
    }

    /**
     * Converts a {@link JsonSchemaElement} to a map.
     *
     * @param jsonSchemaElement the JSON schema element
     * @return the map
     */
    public static Map<String, Object> toMap(JsonSchemaElement jsonSchemaElement) {
        return toMap(jsonSchemaElement, false);
    }

    /**
     * Converts a {@link JsonSchemaElement} to a map.
     *
     * @param jsonSchemaElement the JSON schema element
     * @param strict whether to use strict mode
     * @return the map
     */
    public static Map<String, Object> toMap(JsonSchemaElement jsonSchemaElement, boolean strict) {
        return toMap(jsonSchemaElement, strict, true);
    }

    /**
     * Converts a {@link JsonSchemaElement} to a map.
     *
     * @param jsonSchemaElement the JSON schema element
     * @param strict whether to use strict mode
     * @param required whether the element is required
     * @return the map
     */
    public static Map<String, Object> toMap(JsonSchemaElement jsonSchemaElement, boolean strict, boolean required) {
        if (jsonSchemaElement instanceof JsonObjectSchema jsonObjectSchema) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type("object", strict, required));

            if (jsonObjectSchema.description() != null) {
                map.put("description", jsonObjectSchema.description());
            }

            Map<String, Map<String, Object>> properties = new LinkedHashMap<>();
            jsonObjectSchema
                    .properties()
                    .forEach((property, value) -> properties.put(
                            property,
                            toMap(value, strict, jsonObjectSchema.required().contains(property))));
            map.put("properties", properties);

            if (strict) {
                // When using Structured Outputs with strict=true, all fields must be required.
                // See
                // https://platform.openai.com/docs/guides/structured-outputs/supported-schemas?api-mode=chat#all-fields-must-be-required
                map.put(
                        "required",
                        jsonObjectSchema.properties().keySet().stream().toList());
            } else {
                if (jsonObjectSchema.required() != null) {
                    map.put("required", jsonObjectSchema.required());
                }
            }

            if (strict) {
                map.put("additionalProperties", false);
            }

            if (!jsonObjectSchema.definitions().isEmpty()) {
                map.put("$defs", toMap(jsonObjectSchema.definitions(), strict));
            }

            return map;
        } else if (jsonSchemaElement instanceof JsonArraySchema jsonArraySchema) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type("array", strict, required));
            if (jsonArraySchema.description() != null) {
                map.put("description", jsonArraySchema.description());
            }
            if (jsonArraySchema.items() != null) {
                map.put("items", toMap(jsonArraySchema.items(), strict));
            } else {
                map.put("items", Collections.emptyMap());
            }
            return map;
        } else if (jsonSchemaElement instanceof JsonEnumSchema jsonEnumSchema) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type("string", strict, required));
            if (jsonEnumSchema.description() != null) {
                map.put("description", jsonEnumSchema.description());
            }
            map.put("enum", jsonEnumSchema.enumValues());
            return map;
        } else if (jsonSchemaElement instanceof JsonStringSchema jsonStringSchema) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type("string", strict, required));
            if (jsonStringSchema.description() != null) {
                map.put("description", jsonStringSchema.description());
            }
            return map;
        } else if (jsonSchemaElement instanceof JsonIntegerSchema jsonIntegerSchema) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type("integer", strict, required));
            if (jsonIntegerSchema.description() != null) {
                map.put("description", jsonIntegerSchema.description());
            }
            return map;
        } else if (jsonSchemaElement instanceof JsonNumberSchema jsonNumberSchema) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type("number", strict, required));
            if (jsonNumberSchema.description() != null) {
                map.put("description", jsonNumberSchema.description());
            }
            return map;
        } else if (jsonSchemaElement instanceof JsonBooleanSchema jsonBooleanSchema) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", type("boolean", strict, required));
            if (jsonBooleanSchema.description() != null) {
                map.put("description", jsonBooleanSchema.description());
            }
            return map;
        } else if (jsonSchemaElement instanceof JsonReferenceSchema) {
            Map<String, Object> map = new LinkedHashMap<>();
            String reference = ((JsonReferenceSchema) jsonSchemaElement).reference();
            if (reference != null) {
                map.put("$ref", "#/$defs/" + reference);
            }
            return map;
        } else if (jsonSchemaElement instanceof JsonAnyOfSchema jsonAnyOfSchema) {
            Map<String, Object> map = new LinkedHashMap<>();
            if (jsonAnyOfSchema.description() != null) {
                map.put("description", jsonAnyOfSchema.description());
            }
            List<Map<String, Object>> anyOf = jsonAnyOfSchema.anyOf().stream()
                    .map(element -> toMap(element, strict))
                    .collect(Collectors.toList());
            map.put("anyOf", anyOf);
            return map;
        } else if (jsonSchemaElement instanceof JsonNullSchema) {
            return Map.of("type", "null");
        } else if (jsonSchemaElement instanceof JsonRawSchema jsonNative) {
            @SuppressWarnings("unchecked")
            var map = (Map<String, Object>) Json.fromJson(jsonNative.schema(), Map.class);
            return map;
        } else {
            throw new IllegalArgumentException("Unknown type: " + jsonSchemaElement.getClass());
        }
    }

    private static Object type(String type, boolean strict, boolean required) {
        if (strict && !required) {
            // Emulating an optional parameter by using a union type with null.
            // See
            // https://platform.openai.com/docs/guides/structured-outputs/supported-schemas?api-mode=chat#all-fields-must-be-required
            return new String[] {type, "null"};
        } else {
            return type;
        }
    }

    static boolean isJsonInteger(Class<?> type) {
        return type == byte.class
                || type == Byte.class
                || type == short.class
                || type == Short.class
                || type == int.class
                || type == Integer.class
                || type == long.class
                || type == Long.class
                || type == BigInteger.class;
    }

    static boolean isJsonNumber(Class<?> type) {
        return type == float.class
                || type == Float.class
                || type == double.class
                || type == Double.class
                || type == BigDecimal.class;
    }

    static boolean isJsonBoolean(Class<?> type) {
        return type == boolean.class || type == Boolean.class;
    }

    static boolean isJsonString(Class<?> type) {
        return type == String.class
                || type == char.class
                || type == Character.class
                || CharSequence.class.isAssignableFrom(type)
                || type == UUID.class;
    }

    static boolean isJsonArray(Class<?> type) {
        return type.isArray() || Iterable.class.isAssignableFrom(type);
    }

    /**
     * Metadata for a visited class.
     */
    public static class VisitedClassMetadata {

        /**
         * The JSON schema element.
         */
        public JsonSchemaElement jsonSchemaElement;
        /**
         * The reference.
         */
        public String reference;
        /**
         * Whether recursion was detected.
         */
        public boolean recursionDetected;

        /**
         * Creates a new visited class metadata.
         *
         * @param jsonSchemaElement the JSON schema element
         * @param reference the reference
         * @param recursionDetected whether recursion was detected
         */
        public VisitedClassMetadata(JsonSchemaElement jsonSchemaElement, String reference, boolean recursionDetected) {
            this.jsonSchemaElement = jsonSchemaElement;
            this.reference = reference;
            this.recursionDetected = recursionDetected;
        }
    }
}
