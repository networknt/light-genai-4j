package com.networknt.genai.model.ollama;

import static com.networknt.genai.internal.RetryUtils.withRetryMappingExceptions;
import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.model.ModelProvider.OLLAMA;
import static com.networknt.genai.model.ollama.InternalOllamaHelper.aiMessageFrom;
import static com.networknt.genai.model.ollama.InternalOllamaHelper.chatResponseMetadataFrom;
import static com.networknt.genai.model.ollama.InternalOllamaHelper.toOllamaChatRequest;
import static com.networknt.genai.spi.ServiceHelper.loadFactories;

import com.networknt.genai.model.ModelProvider;
import com.networknt.genai.model.chat.Capability;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.listener.ChatModelListener;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.ollama.spi.OllamaChatModelBuilderFactory;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://github.com/jmorganca/ollama/blob/main/docs/api.md">Ollama API reference</a>
 * <br>
 * <a href="https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values">Ollama API parameters</a>.
 */
public class OllamaChatModel extends OllamaBaseChatModel implements ChatModel {

    private final int maxRetries;

    public OllamaChatModel(OllamaChatModelBuilder builder) {
        init(builder);
        this.maxRetries = getOrDefault(builder.maxRetries, 2);
    }

    @Override
    public ChatResponse doChat(ChatRequest chatRequest) {
        validate(chatRequest.parameters());

        OllamaChatRequest ollamaChatRequest = toOllamaChatRequest(chatRequest, false);
        OllamaChatResponse ollamaChatResponse =
                withRetryMappingExceptions(() -> client.chat(ollamaChatRequest), maxRetries);

        return ChatResponse.builder()
                .aiMessage(aiMessageFrom(ollamaChatResponse.getMessage(), this.returnThinking))
                .metadata(chatResponseMetadataFrom(ollamaChatResponse))
                .build();
    }

    @Override
    public OllamaChatRequestParameters defaultRequestParameters() {
        return defaultRequestParameters;
    }

    @Override
    public List<ChatModelListener> listeners() {
        return listeners;
    }

    @Override
    public ModelProvider provider() {
        return OLLAMA;
    }

    @Override
    public Set<Capability> supportedCapabilities() {
        return supportedCapabilities;
    }

    public static OllamaChatModelBuilder builder() {
        for (OllamaChatModelBuilderFactory factory : loadFactories(OllamaChatModelBuilderFactory.class)) {
            return factory.get();
        }
        return new OllamaChatModelBuilder();
    }

    public static class OllamaChatModelBuilder extends Builder<OllamaChatModel, OllamaChatModelBuilder> {

        private Integer maxRetries;

        public OllamaChatModelBuilder() {
            // This is public so it can be extended
        }

        @Override
        protected OllamaChatModelBuilder self() {
            return this;
        }

        public OllamaChatModelBuilder maxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        @Override
        public OllamaChatModel build() {
            return new OllamaChatModel(this);
        }
    }
}
