package com.networknt.model.ollama;

import static com.networknt.agent.model.ModelProvider.OLLAMA;
import static com.networknt.agent.spi.ServiceHelper.loadFactories;

import com.networknt.agent.model.ModelProvider;
import com.networknt.agent.model.chat.Capability;
import com.networknt.agent.model.chat.StreamingChatModel;
import com.networknt.agent.model.chat.listener.ChatModelListener;
import com.networknt.agent.model.chat.request.ChatRequest;
import com.networknt.agent.model.chat.response.StreamingChatResponseHandler;
import com.networknt.model.ollama.spi.OllamaStreamingChatModelBuilderFactory;
import java.util.List;
import java.util.Set;

/**
 * <a href="https://github.com/jmorganca/ollama/blob/main/docs/api.md">Ollama API reference</a>
 * <br>
 * <a href="https://github.com/jmorganca/ollama/blob/main/docs/modelfile.md#valid-parameters-and-values">Ollama API parameters</a>.
 */
public class OllamaStreamingChatModel extends OllamaBaseChatModel implements StreamingChatModel {

    public OllamaStreamingChatModel(OllamaStreamingChatModelBuilder builder) {
        init(builder);
    }

    @Override
    public void doChat(ChatRequest chatRequest, StreamingChatResponseHandler handler) {
        validate(chatRequest.parameters());
        client.streamingChat(chatRequest, this.returnThinking, handler);
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

    public static OllamaStreamingChatModelBuilder builder() {
        for (OllamaStreamingChatModelBuilderFactory factory :
                loadFactories(OllamaStreamingChatModelBuilderFactory.class)) {
            return factory.get();
        }
        return new OllamaStreamingChatModelBuilder();
    }

    public static class OllamaStreamingChatModelBuilder
            extends Builder<OllamaStreamingChatModel, OllamaStreamingChatModelBuilder> {

        public OllamaStreamingChatModelBuilder() {
            // This is public so it can be extended
        }

        @Override
        protected OllamaStreamingChatModelBuilder self() {
            return this;
        }

        @Override
        public OllamaStreamingChatModel build() {
            return new OllamaStreamingChatModel(this);
        }
    }
}
