package com.networknt.agent.service;

import static com.networknt.agent.internal.Utils.copy;
import static com.networknt.agent.internal.ValidationUtils.ensureNotEmpty;
import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;
import static com.networknt.agent.service.AiServiceParamsUtil.chatRequestParameters;

import com.networknt.agent.Internal;
import com.networknt.agent.tool.ToolSpecification;
import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.guardrail.ChatExecutor;
import com.networknt.agent.guardrail.GuardrailRequestParams;
import com.networknt.agent.invocation.InvocationContext;
import com.networknt.agent.memory.ChatMemory;
import com.networknt.agent.memory.chat.MessageWindowChatMemory;
import com.networknt.agent.model.chat.request.ChatRequest;
import com.networknt.agent.model.chat.response.ChatResponse;
import com.networknt.agent.model.chat.response.PartialResponse;
import com.networknt.agent.model.chat.response.PartialResponseContext;
import com.networknt.agent.model.chat.response.PartialThinking;
import com.networknt.agent.model.chat.response.PartialThinkingContext;
import com.networknt.agent.model.chat.response.PartialToolCall;
import com.networknt.agent.model.chat.response.PartialToolCallContext;
import com.networknt.agent.model.output.TokenUsage;
import com.networknt.agent.observability.api.event.AiServiceRequestIssuedEvent;
import com.networknt.agent.rag.content.Content;
import com.networknt.agent.service.tool.BeforeToolExecution;
import com.networknt.agent.service.tool.ToolArgumentsErrorHandler;
import com.networknt.agent.service.tool.ToolExecution;
import com.networknt.agent.service.tool.ToolExecutionErrorHandler;
import com.networknt.agent.service.tool.ToolExecutor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Internal
public class AiServiceTokenStream implements TokenStream {

    private final List<ChatMessage> messages;

    private final List<ToolSpecification> toolSpecifications;
    private final Map<String, ToolExecutor> toolExecutors;
    private final ToolArgumentsErrorHandler toolArgumentsErrorHandler;
    private final ToolExecutionErrorHandler toolExecutionErrorHandler;
    private final Executor toolExecutor;

    private final List<Content> retrievedContents;
    private final AiServiceContext context;
    private final InvocationContext invocationContext;
    private final GuardrailRequestParams commonGuardrailParams;
    private final Object methodKey;

    private Consumer<String> partialResponseHandler;
    private BiConsumer<PartialResponse, PartialResponseContext> partialResponseWithContextHandler;
    private Consumer<PartialThinking> partialThinkingHandler;
    private BiConsumer<PartialThinking, PartialThinkingContext> partialThinkingWithContextHandler;
    private Consumer<PartialToolCall> partialToolCallHandler;
    private BiConsumer<PartialToolCall, PartialToolCallContext> partialToolCallWithContextHandler;
    private Consumer<List<Content>> contentsHandler;
    private Consumer<ChatResponse> intermediateResponseHandler;
    private Consumer<BeforeToolExecution> beforeToolExecutionHandler;
    private Consumer<ToolExecution> toolExecutionHandler;
    private Consumer<ChatResponse> completeResponseHandler;
    private Consumer<Throwable> errorHandler;

    private int onPartialResponseInvoked;
    private int onPartialResponseWithContextInvoked;
    private int onPartialThinkingInvoked;
    private int onPartialThinkingWithContextInvoked;
    private int onPartialToolCallInvoked;
    private int onPartialToolCallWithContextInvoked;
    private int onIntermediateResponseInvoked;
    private int onCompleteResponseInvoked;
    private int onRetrievedInvoked;
    private int beforeToolExecutionInvoked;
    private int onToolExecutedInvoked;
    private int onErrorInvoked;
    private int ignoreErrorsInvoked;

    /**
     * Creates a new instance of {@link AiServiceTokenStream} with the given parameters.
     *
     * @param parameters the parameters for creating the token stream
     */
    public AiServiceTokenStream(AiServiceTokenStreamParameters parameters) {
        ensureNotNull(parameters, "parameters");
        this.messages = copy(ensureNotEmpty(parameters.messages(), "messages"));
        this.toolSpecifications = copy(parameters.toolSpecifications());
        this.toolExecutors = copy(parameters.toolExecutors());
        this.toolArgumentsErrorHandler = parameters.toolArgumentsErrorHandler();
        this.toolExecutionErrorHandler = parameters.toolExecutionErrorHandler();
        this.toolExecutor = parameters.toolExecutor();
        this.retrievedContents = copy(parameters.retrievedContents());
        this.context = ensureNotNull(parameters.context(), "context");
        ensureNotNull(this.context.streamingChatModel, "streamingChatModel");
        this.invocationContext = parameters.invocationContext();
        this.commonGuardrailParams = parameters.commonGuardrailParams();
        this.methodKey = parameters.methodKey();
    }

    @Override
    public TokenStream onPartialResponse(Consumer<String> partialResponseHandler) {
        this.partialResponseHandler = partialResponseHandler;
        this.onPartialResponseInvoked++;
        return this;
    }

    @Override
    public TokenStream onPartialResponseWithContext(BiConsumer<PartialResponse, PartialResponseContext> handler) {
        this.partialResponseWithContextHandler = handler;
        this.onPartialResponseWithContextInvoked++;
        return this;
    }

    @Override
    public TokenStream onPartialThinking(Consumer<PartialThinking> partialThinkingHandler) {
        this.partialThinkingHandler = partialThinkingHandler;
        this.onPartialThinkingInvoked++;
        return this;
    }

    @Override
    public TokenStream onPartialThinkingWithContext(BiConsumer<PartialThinking, PartialThinkingContext> handler) {
        this.partialThinkingWithContextHandler = handler;
        this.onPartialThinkingWithContextInvoked++;
        return this;
    }

    @Override
    public TokenStream onPartialToolCall(Consumer<PartialToolCall> partialToolCallHandler) {
        this.partialToolCallHandler = partialToolCallHandler;
        this.onPartialToolCallInvoked++;
        return this;
    }

    @Override
    public TokenStream onPartialToolCallWithContext(BiConsumer<PartialToolCall, PartialToolCallContext> handler) {
        this.partialToolCallWithContextHandler = handler;
        this.onPartialToolCallWithContextInvoked++;
        return this;
    }

    @Override
    public TokenStream onRetrieved(Consumer<List<Content>> contentsHandler) {
        this.contentsHandler = contentsHandler;
        this.onRetrievedInvoked++;
        return this;
    }

    @Override
    public TokenStream onIntermediateResponse(Consumer<ChatResponse> intermediateResponseHandler) {
        this.intermediateResponseHandler = intermediateResponseHandler;
        this.onIntermediateResponseInvoked++;
        return this;
    }

    @Override
    public TokenStream beforeToolExecution(Consumer<BeforeToolExecution> beforeToolExecutionHandler) {
        this.beforeToolExecutionHandler = beforeToolExecutionHandler;
        this.beforeToolExecutionInvoked++;
        return this;
    }

    @Override
    public TokenStream onToolExecuted(Consumer<ToolExecution> toolExecutionHandler) {
        this.toolExecutionHandler = toolExecutionHandler;
        this.onToolExecutedInvoked++;
        return this;
    }

    @Override
    public TokenStream onCompleteResponse(Consumer<ChatResponse> completionHandler) {
        this.completeResponseHandler = completionHandler;
        this.onCompleteResponseInvoked++;
        return this;
    }

    @Override
    public TokenStream onError(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
        this.onErrorInvoked++;
        return this;
    }

    @Override
    public TokenStream ignoreErrors() {
        this.errorHandler = null;
        this.ignoreErrorsInvoked++;
        return this;
    }

    @Override
    public void start() {
        validateConfiguration();

        ChatRequest chatRequest = context.chatRequestTransformer.apply(
                ChatRequest.builder()
                        .messages(messages)
                        .parameters(chatRequestParameters(invocationContext.methodArguments(), toolSpecifications))
                        .build(),
                invocationContext.chatMemoryId());

        ChatExecutor chatExecutor = ChatExecutor.builder(context.streamingChatModel)
                .errorHandler(errorHandler)
                .chatRequest(chatRequest)
                .invocationContext(invocationContext)
                .eventListenerRegistrar(context.eventListenerRegistrar)
                .build();

        var handler = new AiServiceStreamingResponseHandler(
                chatRequest,
                chatExecutor,
                context,
                invocationContext,
                partialResponseHandler,
                partialResponseWithContextHandler,
                partialThinkingHandler,
                partialThinkingWithContextHandler,
                partialToolCallHandler,
                partialToolCallWithContextHandler,
                beforeToolExecutionHandler,
                toolExecutionHandler,
                intermediateResponseHandler,
                completeResponseHandler,
                errorHandler,
                initTemporaryMemory(context, messages),
                new TokenUsage(),
                toolSpecifications,
                toolExecutors,
                context.toolService.maxSequentialToolsInvocations(),
                toolArgumentsErrorHandler,
                toolExecutionErrorHandler,
                toolExecutor,
                commonGuardrailParams,
                methodKey);

        if (contentsHandler != null && retrievedContents != null) {
            contentsHandler.accept(retrievedContents);
        }

        context.eventListenerRegistrar.fireEvent(AiServiceRequestIssuedEvent.builder()
                .invocationContext(invocationContext)
                .request(chatRequest)
                .build());

        context.streamingChatModel.chat(chatRequest, handler);
    }

    private void validateConfiguration() {
        if (onPartialResponseInvoked + onPartialResponseWithContextInvoked > 1) {
            throw new IllegalConfigurationException("One of [onPartialResponse, onPartialResponseWithContext] "
                    + "can be invoked on TokenStream at most 1 time");
        }
        if (onPartialThinkingInvoked + onPartialThinkingWithContextInvoked > 1) {
            throw new IllegalConfigurationException("One of [onPartialThinking, onPartialThinkingWithContext] "
                    + "can be invoked on TokenStream at most 1 time");
        }
        if (onPartialToolCallInvoked + onPartialToolCallWithContextInvoked > 1) {
            throw new IllegalConfigurationException("One of [onPartialToolCall, onPartialToolCallWithContext] can be "
                    + "invoked on TokenStream at most 1 time");
        }
        if (onIntermediateResponseInvoked > 1) {
            throw new IllegalConfigurationException(
                    "onIntermediateResponse can be invoked on TokenStream at most 1 time");
        }
        if (onCompleteResponseInvoked > 1) {
            throw new IllegalConfigurationException("onCompleteResponse can be invoked on TokenStream at most 1 time");
        }
        if (onRetrievedInvoked > 1) {
            throw new IllegalConfigurationException("onRetrieved can be invoked on TokenStream at most 1 time");
        }
        if (beforeToolExecutionInvoked > 1) {
            throw new IllegalConfigurationException("beforeToolExecution can be invoked on TokenStream at most 1 time");
        }
        if (onToolExecutedInvoked > 1) {
            throw new IllegalConfigurationException("onToolExecuted can be invoked on TokenStream at most 1 time");
        }
        if (onErrorInvoked + ignoreErrorsInvoked != 1) {
            throw new IllegalConfigurationException(
                    "One of [onError, ignoreErrors] " + "must be invoked on TokenStream exactly 1 time");
        }
    }

    private ChatMemory initTemporaryMemory(AiServiceContext context, List<ChatMessage> messagesToSend) {
        var chatMemory = MessageWindowChatMemory.withMaxMessages(Integer.MAX_VALUE);

        if (!context.hasChatMemory()) {
            chatMemory.add(messagesToSend);
        }

        return chatMemory;
    }
}
