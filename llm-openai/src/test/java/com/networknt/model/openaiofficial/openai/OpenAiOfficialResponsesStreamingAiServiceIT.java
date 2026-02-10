package com.networknt.model.openaiofficial.openai;

import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.networknt.agent.model.chat.StreamingChatModel;
import com.networknt.agent.model.chat.response.ChatResponseMetadata;
import com.networknt.model.openaiofficial.OpenAiOfficialChatResponseMetadata;
import com.networknt.model.openaiofficial.OpenAiOfficialResponsesStreamingChatModel;
import com.networknt.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.agent.model.output.TokenUsage;
import com.networknt.agent.service.common.AbstractStreamingAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiOfficialResponsesStreamingAiServiceIT extends AbstractStreamingAiServiceIT {

    @Override
    protected List<StreamingChatModel> models() {
        var client = OpenAIOkHttpClient.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        StreamingChatModel model = OpenAiOfficialResponsesStreamingChatModel.builder()
                .client(client)
                .modelName(InternalOpenAiOfficialTestHelper.CHAT_MODEL_NAME.toString())
                .build();

        return List.of(model);
    }

    @Override
    protected Class<? extends ChatResponseMetadata> chatResponseMetadataType(StreamingChatModel streamingChatModel) {
        return OpenAiOfficialChatResponseMetadata.class;
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(StreamingChatModel streamingChatModel) {
        return OpenAiOfficialTokenUsage.class;
    }
}
