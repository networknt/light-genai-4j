package com.networknt.agent.rag;

import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.rag.query.Metadata;

import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

/**
 * Represents a request for {@link ChatMessage} augmentation.
 */
public class AugmentationRequest {

    /**
     * The chat message to be augmented.
     * Currently, only {@link UserMessage} is supported.
     */
    private final ChatMessage chatMessage;

    /**
     * Additional metadata related to the augmentation request.
     */
    private final Metadata metadata;

    public AugmentationRequest(ChatMessage chatMessage, Metadata metadata) {
        this.chatMessage = ensureNotNull(chatMessage, "chatMessage");
        this.metadata = ensureNotNull(metadata, "metadata");
    }

    public ChatMessage chatMessage() {
        return chatMessage;
    }

    public Metadata metadata() {
        return metadata;
    }
}
