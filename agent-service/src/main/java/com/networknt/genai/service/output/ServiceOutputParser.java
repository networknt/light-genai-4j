package com.networknt.genai.service.output;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static com.networknt.genai.service.TypeUtils.getRawClass;
import static com.networknt.genai.service.TypeUtils.resolveFirstGenericParameterClass;
import static com.networknt.genai.service.TypeUtils.resolveFirstGenericParameterType;
import static com.networknt.genai.service.TypeUtils.typeHasRawClass;

import com.networknt.genai.Internal;
import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.model.chat.request.json.JsonSchema;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.output.Response;
import com.networknt.genai.service.Result;
import com.networknt.genai.service.TokenStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

@Internal
public class ServiceOutputParser {

    private final OutputParserFactory outputParserFactory;

    public ServiceOutputParser() {
        this(new DefaultOutputParserFactory());
    }

    ServiceOutputParser(OutputParserFactory outputParserFactory) {
        this.outputParserFactory = ensureNotNull(outputParserFactory, "outputParserFactory");
    }

    public Object parse(ChatResponse chatResponse, Type returnType) {

        if (typeHasRawClass(returnType, Result.class)) {
            // In the case of returnType = Result<List<String>>, returnType will be set to List<String>
            returnType = resolveFirstGenericParameterType(returnType);
        }

        Class<?> rawClass = getRawClass(returnType);

        if (rawClass == Response.class) {
            // legacy
            return Response.from(chatResponse.aiMessage(), chatResponse.tokenUsage(), chatResponse.finishReason());
        }

        if (rawClass == void.class || rawClass == Void.class) {
            return null;
        }

        AiMessage aiMessage = chatResponse.aiMessage();
        if (rawClass == AiMessage.class) {
            return aiMessage;
        }

        return parseText(returnType, rawClass, aiMessage.text());
    }

    public Object parseText(Type returnType, String text) {
        return parseText(returnType, getRawClass(returnType), text);
    }

    private Object parseText(Type returnType, Class<?> rawClass, String text) {
        if (rawClass == String.class) {
            return text;
        }

        Class<?> typeArgumentClass = resolveFirstGenericParameterClass(returnType);
        OutputParser<?> outputParser = outputParserFactory.get(rawClass, typeArgumentClass);
        return outputParser.parse(text);
    }

    public Optional<JsonSchema> jsonSchema(Type returnType) {

        if (typeHasRawClass(returnType, Result.class)) {
            // In the case of returnType = Result<List<String>>, returnType will be set to List<String>
            returnType = resolveFirstGenericParameterType(returnType);
        }

        // In the case of returnType = List<String> these two would be set like:
        // rawClass = List.class
        // typeArgumentClass = String.class
        Class<?> rawClass = getRawClass(returnType);
        Class<?> typeArgumentClass = resolveFirstGenericParameterClass(returnType);

        if (schemaNotRequired(rawClass)) {
            return Optional.empty();
        }

        OutputParser<?> outputParser = outputParserFactory.get(rawClass, typeArgumentClass);
        return outputParser.jsonSchema();
    }

    public String outputFormatInstructions(Type returnType) {

        if (typeHasRawClass(returnType, Result.class)) {
            // In the case of returnType = Result<List<String>>, returnType will be set to List<String>
            returnType = resolveFirstGenericParameterType(returnType);
        }

        // In the case of returnType = List<String> these two would be set like:
        // rawClass = List.class
        // typeArgumentClass = String.class
        Class<?> rawClass = getRawClass(returnType);
        Class<?> typeArgumentClass = resolveFirstGenericParameterClass(returnType);

        if (schemaNotRequired(rawClass)) {
            return "";
        }

        OutputParser<?> outputParser = outputParserFactory.get(rawClass, typeArgumentClass);
        String formatInstructions = outputParser.formatInstructions();
        if (!formatInstructions.startsWith("\nYou must")) {
            formatInstructions = "\nYou must answer strictly in the following format: " + formatInstructions;
        }
        return formatInstructions;
    }

    private static boolean schemaNotRequired(Class<?> type) {
        return type == String.class
                || type == AiMessage.class
                || type == TokenStream.class
                || type == Response.class
                || type == Map.class
                || type == void.class
                || type == Void.class;
    }
}
