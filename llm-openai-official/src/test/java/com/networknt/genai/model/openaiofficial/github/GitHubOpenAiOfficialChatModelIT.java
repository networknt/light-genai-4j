package com.networknt.genai.model.openaiofficial.github;

import static com.networknt.genai.model.openaiofficial.github.InternalGitHubOpenAiOfficialTestHelper.CHAT_MODEL_NAME_ALTERNATE;

import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.common.AbstractChatModelIT;
import com.networknt.genai.model.chat.request.ChatRequestParameters;
import com.networknt.genai.model.chat.response.ChatResponseMetadata;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialChatModel;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialChatRequestParameters;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialChatResponseMetadata;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialTokenUsage;
import com.networknt.genai.model.output.TokenUsage;
import java.util.List;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "GITHUB_TOKEN", matches = ".+")
class GitHubOpenAiOfficialChatModelIT extends AbstractChatModelIT {

    @Override
    protected List<ChatModel> models() {
        return InternalGitHubOpenAiOfficialTestHelper.chatModelsNormalAndJsonStrict();
    }

    @Override
    protected ChatModel createModelWith(ChatRequestParameters parameters) {
        OpenAiOfficialChatModel.Builder openAiChatModelBuilder = OpenAiOfficialChatModel.builder()
                .apiKey(System.getenv("GITHUB_TOKEN"))
                .isGitHubModels(true)
                .defaultRequestParameters(parameters);

        if (parameters.modelName() == null) {
            openAiChatModelBuilder.modelName(CHAT_MODEL_NAME_ALTERNATE);
        }
        return openAiChatModelBuilder.build();
    }

    @Override
    protected String customModelName() {
        return com.openai.models.ChatModel.GPT_4O_2024_11_20.toString();
    }

    @Override
    protected boolean supportsModelNameParameter() {
        return false;
    }

    @Override
    protected ChatRequestParameters createIntegrationSpecificParameters(int maxOutputTokens) {
        return OpenAiOfficialChatRequestParameters.builder()
                .maxOutputTokens(maxOutputTokens)
                .build();
    }

    @Override
    protected Class<? extends ChatResponseMetadata> chatResponseMetadataType(ChatModel chatModel) {
        return OpenAiOfficialChatResponseMetadata.class;
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(ChatModel chatModel) {
        return OpenAiOfficialTokenUsage.class;
    }
}
