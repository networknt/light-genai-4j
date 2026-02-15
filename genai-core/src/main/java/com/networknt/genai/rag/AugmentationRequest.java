package com.networknt.genai.rag;

import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.rag.query.Metadata;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

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

    /**
     * Creates a new augmentation request.
     *
     * @param chatMessage the chat message to be augmented
     * @param metadata    additional metadata
     */
    public AugmentationRequest(ChatMessage chatMessage, Metadata metadata) {
        this.chatMessage = ensureNotNull(chatMessage, "chatMessage");
        this.metadata = ensureNotNull(metadata, "metadata");
    }

    /**
     * @return the chat message to be augmented
     */
    public ChatMessage chatMessage() {
        return chatMessage;
    }

    /**
     * @return additional metadata
     */
    public Metadata metadata() {
        return metadata;
    }
}
