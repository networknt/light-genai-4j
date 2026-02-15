package com.networknt.genai.model.bedrock.common;

import static com.networknt.genai.model.bedrock.TestedModels.AWS_NOVA_LITE;
import static com.networknt.genai.model.bedrock.TestedModels.AWS_NOVA_MICRO;
import static com.networknt.genai.model.bedrock.TestedModels.AWS_NOVA_PRO;
import static com.networknt.genai.model.bedrock.TestedModels.CLAUDE_3_HAIKU;
import static com.networknt.genai.model.bedrock.TestedModels.COHERE_COMMAND_R_PLUS;
import static com.networknt.genai.model.bedrock.TestedModels.MISTRAL_LARGE;

import com.networknt.genai.model.bedrock.BedrockTokenUsage;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.output.TokenUsage;
import com.networknt.genai.service.common.AbstractAiServiceIT;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "AWS_SECRET_ACCESS_KEY", matches = ".+")
public class BedrockAiServicesIT extends AbstractAiServiceIT {

    @Override
    protected List<ChatModel> models() {
        return List.of(
                AWS_NOVA_MICRO, AWS_NOVA_LITE, AWS_NOVA_PRO, COHERE_COMMAND_R_PLUS, MISTRAL_LARGE, CLAUDE_3_HAIKU);
    }

    @Override
    protected Class<? extends TokenUsage> tokenUsageType(ChatModel chatModel) {
        return BedrockTokenUsage.class;
    }

    @AfterEach
    void afterEach() {
        sleepIfNeeded();
    }

    public static void sleepIfNeeded() {
        sleepIfNeeded(1);
    }

    public static void sleepIfNeeded(int multiplier) {
        try {
            String ciDelaySeconds = System.getenv("CI_DELAY_SECONDS_BEDROCK");
            if (ciDelaySeconds != null) {
                Thread.sleep(Integer.parseInt(ciDelaySeconds) * 1000L * multiplier);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
