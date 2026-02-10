package com.networknt.model.openaiofficial.openai;

import static java.util.Collections.singletonList;

import com.networknt.agent.model.chat.ChatModel;
import com.networknt.agent.model.chat.common.AbstractChatModelListenerIT;
import com.networknt.agent.model.chat.listener.ChatModelListener;
import com.networknt.model.openaiofficial.OpenAiOfficialChatModel;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiOfficialChatModelListenerIT extends AbstractChatModelListenerIT {

    @Override
    protected ChatModel createModel(ChatModelListener listener) {
        return OpenAiOfficialChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(modelName())
                .temperature(temperature())
                .topP(topP())
                .maxCompletionTokens(maxTokens())
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected String modelName() {
        return com.openai.models.ChatModel.GPT_4O_MINI.toString();
    }

    @Override
    protected ChatModel createFailingModel(ChatModelListener listener) {
        return OpenAiOfficialChatModel.builder()
                .apiKey("banana")
                .modelName(modelName())
                .maxRetries(0)
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected Class<? extends Exception> expectedExceptionClass() {
        return com.openai.errors.UnauthorizedException.class;
    }
}
