package com.networknt.genai.service.common;

import com.networknt.genai.tool.Tool;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.output.TokenUsage;
import com.networknt.genai.service.AiServices;
import com.networknt.genai.service.Result;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.networknt.genai.model.output.FinishReason.STOP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
// import static org.mockito.Mockito.spy;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * This test makes sure that all {@link ChatModel} implementations behave consistently
 * when used with {@link AiServices}.
 */
@TestInstance(PER_CLASS)
public abstract class AbstractAiServiceIT {

    protected abstract List<ChatModel> models();

    protected List<ChatModel> modelsSupportingToolsAndJsonResponseFormatWithSchema() {
        return models();
    }

    interface Assistant {

        Result<String> chat(String userMessage);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_answer_simple_question(ChatModel model) {

        // given
        SpyingChatModel spyModel = new SpyingChatModel(model);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(spyModel)
                .build();

        String userMessage = "What is the capital of Germany?";

        // when
        Result<String> result = assistant.chat(userMessage);

        // then
        assertThat(result.content()).containsIgnoringCase("Berlin");

        assertTokenUsage(result.tokenUsage(), model);

        if (assertFinishReason()) {
            assertThat(result.finishReason()).isEqualTo(STOP);
        }

        assertThat(result.sources()).isEmpty();

        assertThat(result.toolExecutions()).isEmpty();

        // verify(model).chat(ChatRequest.builder().messages(UserMessage.from(userMessage)).build());
        assertThat(spyModel.chatRequests).hasSize(1);
        assertThat(spyModel.chatRequests.get(0).messages()).contains(UserMessage.from(userMessage));
    }

    // TODO more tests for tools
    // TODO more tests for str outputs

    @ParameterizedTest
    @MethodSource("modelsSupportingToolsAndJsonResponseFormatWithSchema")
    @EnabledIf("supportsToolsAndJsonResponseFormatWithSchema")
    void should_execute_tool_then_return_structured_output(ChatModel model) {

        // TODO fail if model does not support RESPONSE_FORMAT_JSON_SCHEMA and tools

        // given
        SpyingChatModel spyModel = new SpyingChatModel(model);

        enum Weather {
            SUNNY, RAINY
        }

        record WeatherReport(String city, Weather weather) {}

        interface WeatherAssistant {
            Result<WeatherReport> chat(String city);
        }

        class WeatherTools {

            @Tool
            String getWeather(String city) {
                return "sunny";
            }
        }

        class SpyingWeatherTools extends WeatherTools {
            final AtomicInteger getWeatherCount = new AtomicInteger();
            final List<String> getWeatherArgs = new ArrayList<>();

            @Override
            String getWeather(String city) {
                getWeatherCount.incrementAndGet();
                getWeatherArgs.add(city);
                return super.getWeather(city);
            }
        }

        SpyingWeatherTools weatherTools = new SpyingWeatherTools();

        WeatherAssistant weatherAssistant = AiServices.builder(WeatherAssistant.class)
                .chatModel(spyModel)
                .tools(weatherTools)
                .build();

        String userMessage = "What is the weather in Munich? Use 'getWeather' tool";

        // when
        Result<WeatherReport> result = weatherAssistant.chat(userMessage);
        WeatherReport weatherReport = result.content();

        // then
        assertThat(weatherReport.city()).isEqualTo("Munich");
        assertThat(weatherReport.weather()).isEqualTo(Weather.SUNNY);

        // TODO
//        verify(model).chat(ChatRequest.builder()
//                .messages(UserMessage.from(userMessage))
//                .parameters(ChatParameters.builder()
//                        .toolSpecifications(ToolSpecifications.toolSpecificationsFrom(WeatherTools.class))
//                        .responseFormat(ResponseFormat.builder()
//                                .type(ResponseFormatType.JSON)
//                                .jsonSchema(JsonSchemas.jsonSchemaFrom(WeatherReport.class).get())
//                                .build())
//                        .build())
//                .build());
//        verify(model).chat(ChatRequest.builder()
//                .messages(
//                        UserMessage.from(userMessage),
//                        AiMessage.from(...),
//                        ToolExecutionResultMessage.from(...)
//                )
//                .parameters(ChatParameters.builder()
//                .toolSpecifications(ToolSpecifications.toolSpecificationsFrom(WeatherTools.class))
//                .responseFormat(ResponseFormat.builder()
//                        .type(ResponseFormatType.JSON)
//                        .jsonSchema(JsonSchemas.jsonSchemaFrom(WeatherReport.class).get())
//                        .build())
//                .build())
//                .build());
//        verifyNoMoreInteractions(model);

        assertTokenUsage(result.tokenUsage(), model);

        if (assertToolInteractions()) {
            // verify(weatherTools).getWeather("Munich");
            // verifyNoMoreInteractions(weatherTools);
            assertThat(weatherTools.getWeatherCount.get()).isEqualTo(1);
            assertThat(weatherTools.getWeatherArgs).containsExactly("Munich");
        }
    }

    protected boolean supportsTools() {
        return true;
    }

    protected boolean supportsJsonResponseFormatWithSchema() {
        return true;
    }

    protected boolean supportsToolsAndJsonResponseFormatWithSchema() {
        return supportsTools() && supportsJsonResponseFormatWithSchema();
    }

    protected boolean assertTokenUsage() {
        return true;
    }

    private void assertTokenUsage(TokenUsage tokenUsage, ChatModel chatModel) {
        assertThat(tokenUsage).isExactlyInstanceOf(tokenUsageType(chatModel));
        assertThat(tokenUsage.inputTokenCount()).isPositive();
        assertThat(tokenUsage.outputTokenCount()).isPositive();
        assertThat(tokenUsage.totalTokenCount())
                .isEqualTo(tokenUsage.inputTokenCount() + tokenUsage.outputTokenCount());
    }

    protected Class<? extends TokenUsage> tokenUsageType(ChatModel chatModel) {
        return TokenUsage.class;
    }

    protected boolean assertFinishReason() {
        return true;
    }

    protected boolean assertToolInteractions() {
        return true;
    }
    
    static class SpyingChatModel implements ChatModel {
        private final ChatModel delegate;
        final List<ChatRequest> chatRequests = new ArrayList<>();

        SpyingChatModel(ChatModel delegate) {
            this.delegate = delegate;
        }

        @Override
        public ChatResponse chat(ChatRequest chatRequest) {
            chatRequests.add(chatRequest);
            return delegate.chat(chatRequest);
        }
    }
}
