package com.networknt.genai.model.anthropic;

import static com.networknt.genai.model.anthropic.AnthropicChatModelName.CLAUDE_3_5_HAIKU_20241022;
import static java.util.Collections.singletonList;

import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.common.AbstractChatModelListenerIT;
import com.networknt.genai.model.chat.listener.ChatModelListener;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class AnthropicChatModelListenerIT extends AbstractChatModelListenerIT {

    @Override
    protected ChatModel createModel(ChatModelListener listener) {
        return AnthropicChatModel.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .modelName(modelName())
                .temperature(temperature())
                .topP(topP())
                .maxTokens(maxTokens())
                .logRequests(true)
                .logResponses(true)
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected String modelName() {
        return CLAUDE_3_5_HAIKU_20241022.toString();
    }

    @Override
    protected ChatModel createFailingModel(ChatModelListener listener) {
        return AnthropicChatModel.builder()
                .apiKey("banana")
                .modelName(modelName())
                .maxRetries(0)
                .logRequests(true)
                .logResponses(true)
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected Class<? extends Exception> expectedExceptionClass() {
        return com.networknt.genai.exception.AuthenticationException.class;
    }
}
