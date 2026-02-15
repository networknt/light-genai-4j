package com.networknt.genai.service.common.openai;

import static com.networknt.genai.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.response.ChatResponseMetadata;
import com.networknt.genai.model.openai.OpenAiChatResponseMetadata;
import com.networknt.genai.model.openai.OpenAiStreamingChatModel;
import com.networknt.genai.model.openai.OpenAiTokenUsage;
import com.networknt.genai.model.output.TokenUsage;
import com.networknt.genai.service.common.AbstractStreamingAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

// TODO move to langchain4j-open-ai module once dependency cycle is resolved
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiStreamingAiServiceIT extends AbstractStreamingAiServiceIT {

    private static OpenAiStreamingChatModel.OpenAiStreamingChatModelBuilder defaultStreamingModelBuilder() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(System.getenv("OPENAI_BASE_URL"))
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
                .modelName(GPT_4_O_MINI);
    }

    @Override
    protected List<StreamingChatModel> models() {
        return List.of(
                defaultStreamingModelBuilder().build()
                // TODO more configs?
                );
    }

    @Override
    protected Class<? extends ChatResponseMetadata> chatResponseMetadataType(StreamingChatModel streamingChatModel) {
        return OpenAiChatResponseMetadata.class;
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(StreamingChatModel streamingChatModel) {
        return OpenAiTokenUsage.class;
    }
}
