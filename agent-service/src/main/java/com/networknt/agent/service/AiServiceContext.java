package com.networknt.agent.service;

import static com.networknt.agent.spi.ServiceHelper.loadFactory;

import com.networknt.agent.Internal;
import com.networknt.agent.memory.ChatMemory;
import com.networknt.agent.memory.chat.ChatMemoryProvider;
import com.networknt.agent.model.chat.ChatModel;
import com.networknt.agent.model.chat.StreamingChatModel;
import com.networknt.agent.model.chat.request.ChatRequest;
import com.networknt.agent.model.moderation.ModerationModel;
import com.networknt.agent.observability.api.AiServiceListenerRegistrar;
import com.networknt.agent.rag.RetrievalAugmentor;
import com.networknt.agent.service.guardrail.GuardrailService;
import com.networknt.agent.service.memory.ChatMemoryService;
import com.networknt.agent.service.tool.ToolService;
import com.networknt.agent.spi.services.AiServiceContextFactory;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

@Internal
public class AiServiceContext {

    private static final Function<Object, Optional<String>> DEFAULT_USER_MESSAGE_PROVIDER = x -> Optional.empty();
    private static final Function<Object, Optional<String>> DEFAULT_SYSTEM_MESSAGE_PROVIDER = x -> Optional.empty();

    public final Class<?> aiServiceClass;
    public final AiServiceListenerRegistrar eventListenerRegistrar = AiServiceListenerRegistrar.newInstance();

    public Class<?> returnType;

    public ChatModel chatModel;
    public StreamingChatModel streamingChatModel;

    public ChatMemoryService chatMemoryService;

    public ToolService toolService = new ToolService();

    public final GuardrailService.Builder guardrailServiceBuilder;
    private final AtomicReference<GuardrailService> guardrailService = new AtomicReference<>();

    public ModerationModel moderationModel;

    public RetrievalAugmentor retrievalAugmentor;

    public boolean storeRetrievedContentInChatMemory = true;

    public Function<Object, Optional<String>> userMessageProvider = DEFAULT_USER_MESSAGE_PROVIDER;
    public Function<Object, Optional<String>> systemMessageProvider = DEFAULT_SYSTEM_MESSAGE_PROVIDER;

    public BiFunction<ChatRequest, Object, ChatRequest> chatRequestTransformer = (req, memId) -> req;

    protected AiServiceContext(Class<?> aiServiceClass) {
        this.aiServiceClass = aiServiceClass;
        this.guardrailServiceBuilder = GuardrailService.builder(aiServiceClass);
    }

    private static class FactoryHolder {
        private static final AiServiceContextFactory contextFactory = loadFactory(AiServiceContextFactory.class);
    }

    public static AiServiceContext create(Class<?> aiServiceClass) {
        return FactoryHolder.contextFactory != null
                ? FactoryHolder.contextFactory.create(aiServiceClass)
                : new AiServiceContext(aiServiceClass);
    }

    public boolean hasChatMemory() {
        return chatMemoryService != null;
    }

    public void initChatMemories(ChatMemory chatMemory) {
        chatMemoryService = new ChatMemoryService(chatMemory);
    }

    public void initChatMemories(ChatMemoryProvider chatMemoryProvider) {
        chatMemoryService = new ChatMemoryService(chatMemoryProvider);
    }

    public boolean hasModerationModel() {
        return moderationModel != null;
    }

    public GuardrailService guardrailService() {
        return this.guardrailService.updateAndGet(
                service -> (service != null) ? service : guardrailServiceBuilder.build());
    }
}
