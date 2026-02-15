package com.networknt.genai.model.openai.internal;

import static com.networknt.genai.internal.Exceptions.illegalArgument;
import static com.networknt.genai.internal.JsonSchemaElementUtils.toMap;
import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.Utils.isNotNullOrBlank;
import static com.networknt.genai.internal.Utils.isNullOrBlank;
import static com.networknt.genai.internal.Utils.isNullOrEmpty;
import static com.networknt.genai.internal.ValidationUtils.ensureNotBlank;
import static com.networknt.genai.model.chat.request.ResponseFormat.JSON;
import static com.networknt.genai.model.chat.request.ResponseFormatType.TEXT;
import static com.networknt.genai.model.openai.internal.chat.ResponseFormatType.JSON_OBJECT;
import static com.networknt.genai.model.openai.internal.chat.ResponseFormatType.JSON_SCHEMA;
import static com.networknt.genai.model.openai.internal.chat.ToolType.FUNCTION;
import static com.networknt.genai.model.output.FinishReason.CONTENT_FILTER;
import static com.networknt.genai.model.output.FinishReason.LENGTH;
import static com.networknt.genai.model.output.FinishReason.STOP;
import static com.networknt.genai.model.output.FinishReason.TOOL_EXECUTION;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import com.networknt.genai.Internal;
import com.networknt.genai.tool.ToolExecutionRequest;
import com.networknt.genai.tool.ToolSpecification;
import com.networknt.genai.data.image.Image;
import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.AudioContent;
import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.Content;
import com.networknt.genai.data.message.ImageContent;
import com.networknt.genai.data.message.PdfFileContent;
import com.networknt.genai.data.message.SystemMessage;
import com.networknt.genai.data.message.TextContent;
import com.networknt.genai.data.message.ToolExecutionResultMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.data.message.VideoContent;
import com.networknt.genai.data.video.Video;
import com.networknt.genai.exception.ContentFilteredException;
import com.networknt.genai.exception.UnsupportedFeatureException;
import com.networknt.genai.model.audio.AudioTranscriptionRequest;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.request.ChatRequestParameters;
import com.networknt.genai.model.chat.request.ResponseFormat;
import com.networknt.genai.model.chat.request.ToolChoice;
import com.networknt.genai.model.chat.request.json.JsonObjectSchema;
import com.networknt.genai.model.chat.request.json.JsonRawSchema;
import com.networknt.genai.model.chat.request.json.JsonSchema;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.openai.OpenAiChatRequestParameters;
import com.networknt.genai.model.openai.OpenAiTokenUsage;
import com.networknt.genai.model.openai.OpenAiTokenUsage.InputTokensDetails;
import com.networknt.genai.model.openai.OpenAiTokenUsage.OutputTokensDetails;
import com.networknt.genai.model.openai.internal.chat.AssistantMessage;
import com.networknt.genai.model.openai.internal.chat.ChatCompletionRequest;
import com.networknt.genai.model.openai.internal.chat.ChatCompletionResponse;
import com.networknt.genai.model.openai.internal.chat.ContentType;
import com.networknt.genai.model.openai.internal.chat.Function;
import com.networknt.genai.model.openai.internal.chat.FunctionCall;
import com.networknt.genai.model.openai.internal.chat.FunctionMessage;
import com.networknt.genai.model.openai.internal.chat.ImageDetail;
import com.networknt.genai.model.openai.internal.chat.ImageUrl;
import com.networknt.genai.model.openai.internal.chat.InputAudio;
import com.networknt.genai.model.openai.internal.chat.Message;
import com.networknt.genai.model.openai.internal.chat.PdfFile;
import com.networknt.genai.model.openai.internal.chat.Tool;
import com.networknt.genai.model.openai.internal.chat.ToolCall;
import com.networknt.genai.model.openai.internal.chat.ToolChoiceMode;
import com.networknt.genai.model.openai.internal.chat.ToolMessage;
import com.networknt.genai.model.openai.internal.chat.VideoUrl;
import com.networknt.genai.model.openai.internal.shared.CompletionTokensDetails;
import com.networknt.genai.model.openai.internal.shared.PromptTokensDetails;
import com.networknt.genai.model.openai.internal.shared.Usage;
import com.networknt.genai.model.output.FinishReason;
import com.networknt.genai.model.output.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Internal
public class OpenAiUtils {

    public static final String DEFAULT_OPENAI_URL = "https://api.openai.com/v1";
    public static final String DEFAULT_USER_AGENT = "langchain4j-openai";

    public static List<Message> toOpenAiMessages(List<ChatMessage> messages) {
        return toOpenAiMessages(messages, false, null);
    }

    public static List<Message> toOpenAiMessages(List<ChatMessage> messages, boolean sendThinking, String thinkingFieldName) {
        return messages.stream()
                .map(message -> toOpenAiMessage(message, sendThinking, thinkingFieldName))
                .collect(toList());
    }

    public static Message toOpenAiMessage(ChatMessage message) {
        return toOpenAiMessage(message, false, null);
    }

    public static Message toOpenAiMessage(ChatMessage message, boolean sendThinking, String thinkingFieldName) {
        if (message instanceof SystemMessage) {
            return com.networknt.genai.model.openai.internal.chat.SystemMessage.from(((SystemMessage) message).text());
        }

        if (message instanceof UserMessage userMessage) {
            if (userMessage.hasSingleText()) {
                return com.networknt.genai.model.openai.internal.chat.UserMessage.builder()
                        .content(userMessage.singleText())
                        .name(userMessage.name())
                        .build();
            } else {
                return com.networknt.genai.model.openai.internal.chat.UserMessage.builder()
                        .content(userMessage.contents().stream()
                                .map(OpenAiUtils::toOpenAiContent)
                                .collect(toList()))
                        .name(userMessage.name())
                        .build();
            }
        }

        if (message instanceof AiMessage aiMessage) {

            String thinking = null;
            if (sendThinking && !isNullOrEmpty(aiMessage.thinking())) {
                thinking = aiMessage.thinking();
            }

            if (!aiMessage.hasToolExecutionRequests()) {
                AssistantMessage.Builder builder = AssistantMessage.builder()
                        .content(aiMessage.text());
                if (thinking != null) {
                    builder.customParameter(thinkingFieldName, thinking);
                }
                return builder.build();
            }

            ToolExecutionRequest toolExecutionRequest =
                    aiMessage.toolExecutionRequests().get(0);
            if (toolExecutionRequest.id() == null) {
                FunctionCall functionCall = FunctionCall.builder()
                        .name(toolExecutionRequest.name())
                        .arguments(toolExecutionRequest.arguments())
                        .build();

                AssistantMessage.Builder builder = AssistantMessage.builder().functionCall(functionCall);
                if (thinking != null) {
                    builder.customParameter(thinkingFieldName, thinking);
                }
                return builder.build();
            }

            List<ToolCall> toolCalls = aiMessage.toolExecutionRequests().stream()
                    .map(it -> ToolCall.builder()
                            .id(it.id())
                            .type(FUNCTION)
                            .function(FunctionCall.builder()
                                    .name(it.name())
                                    .arguments(isNullOrBlank(it.arguments()) ? "{}" : it.arguments())
                                    .build())
                            .build())
                    .collect(toList());

            AssistantMessage.Builder builder = AssistantMessage.builder()
                    .content(aiMessage.text())
                    .toolCalls(toolCalls);
            if (thinking != null) {
                builder.customParameter(thinkingFieldName, thinking);
            }
            return builder.build();
        }

        if (message instanceof ToolExecutionResultMessage toolExecutionResultMessage) {

            if (toolExecutionResultMessage.id() == null) {
                return FunctionMessage.from(toolExecutionResultMessage.toolName(), toolExecutionResultMessage.text());
            }

            return ToolMessage.from(toolExecutionResultMessage.id(), toolExecutionResultMessage.text());
        }

        throw illegalArgument("Unknown message type: " + message.type());
    }

    private static com.networknt.genai.model.openai.internal.chat.Content toOpenAiContent(Content content) {
        if (content instanceof TextContent) {
            return toOpenAiContent((TextContent) content);
        } else if (content instanceof ImageContent) {
            return toOpenAiContent((ImageContent) content);
        } else if (content instanceof VideoContent videoContent) {
            return toOpenAiContent(videoContent);
        } else if (content instanceof AudioContent audioContent) {
            return toOpenAiContent(audioContent);
        } else if (content instanceof PdfFileContent pdfFileContent) {
            return toOpenAiContent(pdfFileContent);
        } else {
            throw illegalArgument("Unknown content type: " + content);
        }
    }

    private static com.networknt.genai.model.openai.internal.chat.Content toOpenAiContent(TextContent content) {
        return com.networknt.genai.model.openai.internal.chat.Content.builder()
                .type(ContentType.TEXT)
                .text(content.text())
                .build();
    }

    private static com.networknt.genai.model.openai.internal.chat.Content toOpenAiContent(ImageContent content) {
        return com.networknt.genai.model.openai.internal.chat.Content.builder()
                .type(ContentType.IMAGE_URL)
                .imageUrl(ImageUrl.builder()
                        .url(toUrl(content.image()))
                        .detail(toDetail(content.detailLevel()))
                        .build())
                .build();
    }

    private static com.networknt.genai.model.openai.internal.chat.Content toOpenAiContent(VideoContent content) {
        return com.networknt.genai.model.openai.internal.chat.Content.builder()
                .type(ContentType.VIDEO_URL)
                .videoUrl(VideoUrl.builder().url(toVideoUrl(content.video())).build())
                .build();
    }

    private static com.networknt.genai.model.openai.internal.chat.Content toOpenAiContent(AudioContent audioContent) {
        return com.networknt.genai.model.openai.internal.chat.Content.builder()
                .type(ContentType.AUDIO)
                .inputAudio(InputAudio.builder()
                        .data(ensureNotBlank(audioContent.audio().base64Data(), "audio.base64Data"))
                        .format(extractSubtype(
                                ensureNotBlank(audioContent.audio().mimeType(), "audio.mimeType")))
                        .build())
                .build();
    }

    private static com.networknt.genai.model.openai.internal.chat.Content toOpenAiContent(PdfFileContent pdfFileContent) {
        String fileData;
        if (pdfFileContent.pdfFile().url() != null) {
            fileData = pdfFileContent.pdfFile().url().toString();
        } else {
            fileData = format(
                    "data:%s;base64,%s",
                    pdfFileContent.pdfFile().mimeType(),
                    pdfFileContent.pdfFile().base64Data());
        }

        return com.networknt.genai.model.openai.internal.chat.Content.builder()
                .type(ContentType.FILE)
                .file(PdfFile.builder().fileData(fileData).filename("pdf_file").build())
                .build();
    }

    private static String extractSubtype(String mimetype) {
        return mimetype.split("/")[1];
    }

    private static String toUrl(Image image) {
        if (image.url() != null) {
            return image.url().toString();
        }
        return format("data:%s;base64,%s", image.mimeType(), image.base64Data());
    }

    private static String toVideoUrl(Video video) {
        if (video.url() != null) {
            return video.url().toString();
        }
        return format("data:%s;base64,%s", video.mimeType(), video.base64Data());
    }

    private static ImageDetail toDetail(ImageContent.DetailLevel detailLevel) {
        if (detailLevel == null) {
            return null;
        }

        return switch (detailLevel) {
            case LOW -> ImageDetail.LOW;
            case HIGH -> ImageDetail.HIGH;
            case AUTO -> ImageDetail.AUTO;
            default -> throw new UnsupportedFeatureException("Unsupported detail level: " + detailLevel);
        };
    }

    public static List<Tool> toTools(Collection<ToolSpecification> toolSpecifications, boolean strict) {
        return toolSpecifications.stream()
                .map((ToolSpecification toolSpecification) -> toTool(toolSpecification, strict))
                .collect(toList());
    }

    private static Tool toTool(ToolSpecification toolSpecification, boolean strict) {
        Function.Builder functionBuilder = Function.builder()
                .name(toolSpecification.name())
                .description(toolSpecification.description())
                .parameters(toOpenAiParameters(toolSpecification.parameters(), strict));
        if (strict) {
            functionBuilder.strict(true);
        }
        Function function = functionBuilder.build();
        return Tool.from(function);
    }

    /**
     * @deprecated Functions are deprecated by OpenAI, use {@link #toTools(Collection, boolean)} instead
     */
    @Deprecated
    public static List<Function> toFunctions(Collection<ToolSpecification> toolSpecifications) {
        return toolSpecifications.stream().map(OpenAiUtils::toFunction).collect(toList());
    }

    /**
     * @deprecated Functions are deprecated by OpenAI, use {@link #toTool(ToolSpecification, boolean)} instead
     */
    @Deprecated
    private static Function toFunction(ToolSpecification toolSpecification) {
        return Function.builder()
                .name(toolSpecification.name())
                .description(toolSpecification.description())
                .parameters(toOpenAiParameters(toolSpecification.parameters(), false))
                .build();
    }

    private static Map<String, Object> toOpenAiParameters(JsonObjectSchema parameters, boolean strict) {
        if (parameters != null) {
            return toMap(parameters, strict);
        } else {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", "object");
            map.put("properties", new HashMap<>());
            map.put("required", new ArrayList<>());
            if (strict) {
                // When strict, additionalProperties must be false:
                // See
                // https://platform.openai.com/docs/guides/structured-outputs/additionalproperties-false-must-always-be-set-in-objects?api-mode=chat#additionalproperties-false-must-always-be-set-in-objects
                map.put("additionalProperties", false);
            }
            return map;
        }
    }

    public static AiMessage aiMessageFrom(ChatCompletionResponse response) {
        return aiMessageFrom(response, false);
    }

    public static AiMessage aiMessageFrom(ChatCompletionResponse response, boolean returnThinking) {
        AssistantMessage assistantMessage = response.choices().get(0).message();

        String refusal = assistantMessage.refusal();
        if (isNotNullOrBlank(refusal)) {
            throw new ContentFilteredException(refusal);
        }

        String content = assistantMessage.content();

        String reasoningContent = null;
        if (returnThinking) {
            reasoningContent = assistantMessage.reasoningContent();
        }

        List<ToolExecutionRequest> toolExecutionRequests =
                getOrDefault(assistantMessage.toolCalls(), List.of()).stream()
                        .filter(toolCall -> toolCall.type() == FUNCTION)
                        .map(OpenAiUtils::toToolExecutionRequest)
                        .collect(toList());

        // legacy
        FunctionCall functionCall = assistantMessage.functionCall();
        if (functionCall != null) {
            ToolExecutionRequest toolExecutionRequest = ToolExecutionRequest.builder()
                    .name(functionCall.name())
                    .arguments(functionCall.arguments())
                    .build();
            toolExecutionRequests.add(toolExecutionRequest);
        }

        return AiMessage.builder()
                .text(isNullOrEmpty(content) ? null : content)
                .thinking(isNullOrEmpty(reasoningContent) ? null : reasoningContent)
                .toolExecutionRequests(toolExecutionRequests)
                .build();
    }

    private static ToolExecutionRequest toToolExecutionRequest(ToolCall toolCall) {
        FunctionCall functionCall = toolCall.function();
        return ToolExecutionRequest.builder()
                .id(toolCall.id())
                .name(functionCall.name())
                .arguments(functionCall.arguments())
                .build();
    }

    public static OpenAiTokenUsage tokenUsageFrom(Usage openAiUsage) {
        if (openAiUsage == null) {
            return null;
        }

        PromptTokensDetails promptTokensDetails = openAiUsage.promptTokensDetails();
        InputTokensDetails inputTokensDetails = null;
        if (promptTokensDetails != null) {
            inputTokensDetails = InputTokensDetails.builder()
                    .cachedTokens(promptTokensDetails.cachedTokens())
                    .build();
        }

        CompletionTokensDetails completionTokensDetails = openAiUsage.completionTokensDetails();
        OutputTokensDetails outputTokensDetails = null;
        if (completionTokensDetails != null) {
            outputTokensDetails = OutputTokensDetails.builder()
                    .reasoningTokens(completionTokensDetails.reasoningTokens())
                    .build();
        }

        return OpenAiTokenUsage.builder()
                .inputTokenCount(openAiUsage.promptTokens())
                .inputTokensDetails(inputTokensDetails)
                .outputTokenCount(openAiUsage.completionTokens())
                .outputTokensDetails(outputTokensDetails)
                .totalTokenCount(openAiUsage.totalTokens())
                .build();
    }

    public static FinishReason finishReasonFrom(String openAiFinishReason) {
        if (openAiFinishReason == null) {
            return null;
        }
        switch (openAiFinishReason) {
            case "stop":
                return STOP;
            case "length":
                return LENGTH;
            case "tool_calls":
            case "function_call":
                return TOOL_EXECUTION;
            case "content_filter":
                return CONTENT_FILTER;
            default:
                return null;
        }
    }

    static com.networknt.genai.model.openai.internal.chat.ResponseFormat toOpenAiResponseFormat(
            ResponseFormat responseFormat, Boolean strict) {
        if (responseFormat == null || responseFormat.type() == TEXT) {
            return null;
        }

        JsonSchema jsonSchema = responseFormat.jsonSchema();
        if (jsonSchema == null) {
            return com.networknt.genai.model.openai.internal.chat.ResponseFormat.builder()
                    .type(JSON_OBJECT)
                    .build();
        } else {
            if (!(jsonSchema.rootElement() instanceof JsonObjectSchema
                    || jsonSchema.rootElement() instanceof JsonRawSchema)) {
                throw new IllegalArgumentException(
                        "For OpenAI, the root element of the JSON Schema must be either a JsonObjectSchema or a JsonRawSchema, but it was: "
                                + jsonSchema.rootElement().getClass());
            }
            com.networknt.genai.model.openai.internal.chat.JsonSchema openAiJsonSchema =
                    com.networknt.genai.model.openai.internal.chat.JsonSchema.builder()
                            .name(jsonSchema.name())
                            .strict(strict)
                            .schema(toMap(jsonSchema.rootElement(), strict))
                            .build();
            return com.networknt.genai.model.openai.internal.chat.ResponseFormat.builder()
                    .type(JSON_SCHEMA)
                    .jsonSchema(openAiJsonSchema)
                    .build();
        }
    }

    public static ToolChoiceMode toOpenAiToolChoice(ToolChoice toolChoice) {
        if (toolChoice == null) {
            return null;
        }

        return switch (toolChoice) {
            case AUTO -> ToolChoiceMode.AUTO;
            case REQUIRED -> ToolChoiceMode.REQUIRED;
            case NONE -> ToolChoiceMode.NONE;
        };
    }

    public static Response<AiMessage> convertResponse(ChatResponse chatResponse) {
        return Response.from(
                chatResponse.aiMessage(),
                chatResponse.metadata().tokenUsage(),
                chatResponse.metadata().finishReason());
    }

    public static void validate(ChatRequestParameters parameters) {
        if (parameters.topK() != null) {
            throw new UnsupportedFeatureException("'topK' parameter is not supported by OpenAI");
        }
    }

    public static ResponseFormat fromOpenAiResponseFormat(String responseFormat) {
        if ("json_object".equals(responseFormat)) {
            return JSON;
        } else {
            return null;
        }
    }

    public static ChatCompletionRequest.Builder toOpenAiChatRequest(
            ChatRequest chatRequest,
            OpenAiChatRequestParameters parameters,
            Boolean strictTools,
            Boolean strictJsonSchema) {
        return toOpenAiChatRequest(chatRequest, parameters, false, null, strictTools, strictJsonSchema);
    }

    public static ChatCompletionRequest.Builder toOpenAiChatRequest(
            ChatRequest chatRequest,
            OpenAiChatRequestParameters parameters,
            boolean sendThinking,
            String thinkingFieldName,
            Boolean strictTools,
            Boolean strictJsonSchema) {

        return ChatCompletionRequest.builder()
                .messages(toOpenAiMessages(chatRequest.messages(), sendThinking, thinkingFieldName))
                // common parameters
                .model(parameters.modelName())
                .temperature(parameters.temperature())
                .topP(parameters.topP())
                .frequencyPenalty(parameters.frequencyPenalty())
                .presencePenalty(parameters.presencePenalty())
                .maxTokens(parameters.maxOutputTokens())
                .stop(parameters.stopSequences())
                .tools(toTools(parameters.toolSpecifications(), strictTools))
                .toolChoice(toOpenAiToolChoice(parameters.toolChoice()))
                .responseFormat(toOpenAiResponseFormat(parameters.responseFormat(), strictJsonSchema))
                // OpenAI-specific parameters
                .maxCompletionTokens(parameters.maxCompletionTokens())
                .logitBias(parameters.logitBias())
                .parallelToolCalls(parameters.parallelToolCalls())
                .seed(parameters.seed())
                .user(parameters.user())
                .store(parameters.store())
                .metadata(parameters.metadata())
                .serviceTier(parameters.serviceTier())
                .reasoningEffort(parameters.reasoningEffort())
                .customParameters(parameters.customParameters());
    }
}
