package com.networknt.genai.guardrail;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.Internal;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;

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
