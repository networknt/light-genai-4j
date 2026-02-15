package com.networknt.genai.model.openai.compatible.deepseek;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.common.AbstractStreamingChatModelIT;
import com.networknt.genai.model.chat.common.ChatResponseAndStreamingMetadata;
import com.networknt.genai.model.chat.common.StreamingMetadata;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.chat.response.ChatResponseMetadata;
import com.networknt.genai.model.openai.OpenAiStreamingChatModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static com.networknt.genai.model.output.FinishReason.STOP;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "DEEPSEEK_API_KEY", matches = ".+")
class OpenAiStreamingChatModelDeepSeekIT { // TODO extends AbstractStreamingChatModelIT

    @Test
    protected void should_respect_user_message() {

        // given
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .modelName("deepseek-chat")
                .logRequests(false) // base64-encoded images are huge in logs
                .logResponses(true)
                .build();

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(UserMessage.from("What is the capital of Germany?"))
                .build();

        // when
        ChatResponseAndStreamingMetadata chatResponseAndStreamingMetadata =
                AbstractStreamingChatModelIT.chat(model, chatRequest, ignored -> {
                }, 60, true);
        ChatResponse chatResponse = chatResponseAndStreamingMetadata.chatResponse();

        // then
        AiMessage aiMessage = chatResponse.aiMessage();
        assertThat(aiMessage.text()).containsIgnoringCase("Berlin");
        assertThat(aiMessage.toolExecutionRequests()).isEmpty();

        ChatResponseMetadata chatResponseMetadata = chatResponse.metadata();
        assertThat(chatResponseMetadata.id()).isNotBlank();
        assertThat(chatResponseMetadata.modelName()).isNotBlank();
        assertThat(chatResponseMetadata.finishReason()).isEqualTo(STOP);

        StreamingMetadata streamingMetadata = chatResponseAndStreamingMetadata.streamingMetadata();
        assertThat(streamingMetadata.concatenatedPartialResponses()).isEqualTo(aiMessage.text());
        assertThat(streamingMetadata.timesOnPartialResponseWasCalled()).isGreaterThan(1);
        assertThat(streamingMetadata.partialToolCalls()).isEmpty();
        assertThat(streamingMetadata.completeToolCalls()).isEmpty();
        assertThat(streamingMetadata.timesOnCompleteResponseWasCalled()).isEqualTo(1);
    }
}
