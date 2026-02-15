package com.networknt.genai.model.bedrock.common;

import static com.networknt.genai.model.bedrock.TestedModels.*;
import static com.networknt.genai.model.bedrock.common.BedrockAiServicesIT.sleepIfNeeded;
import static com.networknt.genai.model.output.FinishReason.STOP;
import static org.assertj.core.api.Assertions.assertThat;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.model.bedrock.BedrockChatModel;
import com.networknt.genai.model.bedrock.BedrockChatResponseMetadata;
import com.networknt.genai.model.bedrock.BedrockTokenUsage;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.common.AbstractChatModelIT;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.request.ChatRequestParameters;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.chat.response.ChatResponseMetadata;
import com.networknt.genai.model.output.TokenUsage;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@EnabledIfEnvironmentVariable(named = "AWS_SECRET_ACCESS_KEY", matches = ".+")
class BedrockChatModelNovaWithVisionIT extends AbstractChatModelIT {

    @Override
    protected List<ChatModel> models() {
        return List.of(AWS_NOVA_LITE, AWS_NOVA_PRO);
    }

    @Override
    protected String customModelName() {
        return "cohere.command-r-v1:0";
    }

    @Override
    protected ChatRequestParameters createIntegrationSpecificParameters(int maxOutputTokens) {
        return ChatRequestParameters.builder().maxOutputTokens(maxOutputTokens).build();
    }

    @Override
    protected ChatModel createModelWith(ChatRequestParameters parameters) {
        return BedrockChatModel.builder()
                .defaultRequestParameters(parameters)
                // force a working model with stopSequence parameter for @Tests
                .modelId("cohere.command-r-v1:0")
                .build();
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(ChatModel model) {
        return BedrockTokenUsage.class;
    }

    @Override
    protected boolean supportsJsonResponseFormat() {
        return false; // output format not supported
    }

    @Override
    protected boolean supportsJsonResponseFormatWithSchema() {
        return false; // output format not supported
    }

    @Override
    protected boolean supportsJsonResponseFormatWithRawSchema() {
        return false; // output format not supported
    }

    @Override
    protected Class<? extends ChatResponseMetadata> chatResponseMetadataType(final ChatModel model) {
        return BedrockChatResponseMetadata.class;
    }

    // OVERRIDE BECAUSE OF INCOHERENCY IN STOPSEQUENCE MANAGEMENT (Nova models include stopSequence)
    @Override
    @ParameterizedTest
    @MethodSource("models")
    @EnabledIf("supportsStopSequencesParameter")
    protected void should_respect_stopSequences_in_chat_request(ChatModel model) {

        // given
        List<String> stopSequences = List.of("Hello", " Hello");
        ChatRequestParameters parameters =
                ChatRequestParameters.builder().stopSequences(stopSequences).build();

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(UserMessage.from("Say 'Hello World'"))
                .parameters(parameters)
                .build();

        // when
        ChatResponse chatResponse = chat(model, chatRequest).chatResponse();

        // then
        AiMessage aiMessage = chatResponse.aiMessage();
        assertThat(aiMessage.text()).containsIgnoringCase("Hello");
        assertThat(aiMessage.text()).doesNotContainIgnoringCase("World");
        assertThat(aiMessage.toolExecutionRequests()).isEmpty();

        if (assertFinishReason()) {
            assertThat(chatResponse.metadata().finishReason()).isEqualTo(STOP);
        }
    }

    @AfterEach
    void afterEach() {
        sleepIfNeeded();
    }
}
