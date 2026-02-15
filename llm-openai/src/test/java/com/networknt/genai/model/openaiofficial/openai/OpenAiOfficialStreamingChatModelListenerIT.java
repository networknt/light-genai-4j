package com.networknt.genai.model.openaiofficial.openai;

import static java.util.Collections.singletonList;

import com.openai.models.ChatModel;
import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.common.AbstractStreamingChatModelListenerIT;
import com.networknt.genai.model.chat.listener.ChatModelListener;
import com.networknt.genai.model.openaiofficial.OpenAiOfficialStreamingChatModel;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiOfficialStreamingChatModelListenerIT extends AbstractStreamingChatModelListenerIT {

    @Override
    protected StreamingChatModel createModel(ChatModelListener listener) {
        return OpenAiOfficialStreamingChatModel.builder()
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
        return ChatModel.GPT_4O_MINI.toString();
    }

    @Override
    protected StreamingChatModel createFailingModel(ChatModelListener listener) {
        return OpenAiOfficialStreamingChatModel.builder()
                .apiKey("banana")
                .modelName(modelName())
                .listeners(singletonList(listener))
                .build();
    }

    @Override
    protected Class<? extends Exception> expectedExceptionClass() {
        return java.util.concurrent.CompletionException.class; // TODO fix
    }
}
