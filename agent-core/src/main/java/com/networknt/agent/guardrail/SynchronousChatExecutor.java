package com.networknt.agent.guardrail;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

import com.networknt.agent.Internal;
import com.networknt.agent.model.chat.ChatModel;
import com.networknt.agent.model.chat.request.ChatRequest;
import com.networknt.agent.model.chat.response.ChatResponse;

/**
 * A concrete implementation of the {@link ChatExecutor} interface that executes
 * chat requests using a specified {@link ChatModel}.
 *
 * This class utilizes a {@link ChatRequest} to encapsulate the input messages
 * and parameters and delegates the execution of the chat to the provided
 * {@link ChatModel}.
 *
 * Instances of this class are immutable and are typically instantiated using
 * the {@link SynchronousBuilder}.
 */
@Internal
final class SynchronousChatExecutor extends AbstractChatExecutor {
    private final ChatModel chatModel;

    protected SynchronousChatExecutor(SynchronousBuilder builder) {
        super(builder);
        this.chatModel = ensureNotNull(builder.chatModel, "chatModel");
    }

    @Override
    protected ChatResponse execute(ChatRequest chatRequest) {
        return this.chatModel.chat(chatRequest);
    }
}
