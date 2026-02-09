package com.networknt.agent.rag;

import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.rag.content.Content;

import java.util.List;

import static com.networknt.agent.internal.Utils.copy;
import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;

/**
 * Represents the result of a {@link ChatMessage} augmentation.
 */
public class AugmentationResult {

    /**
     * The augmented chat message.
     */
    private final ChatMessage chatMessage;

    /**
     * A list of content used to augment the original chat message.
     */
    private final List<Content> contents;

    public AugmentationResult(ChatMessage chatMessage, List<Content> contents) {
        this.chatMessage = ensureNotNull(chatMessage, "chatMessage");
        this.contents = copy(contents);
    }

    public static AugmentationResultBuilder builder() {
        return new AugmentationResultBuilder();
    }

    public ChatMessage chatMessage() {
        return chatMessage;
    }

    public List<Content> contents() {
        return contents;
    }

    public static class AugmentationResultBuilder {

        private ChatMessage chatMessage;
        private List<Content> contents;

        AugmentationResultBuilder() {
        }

        public AugmentationResultBuilder chatMessage(ChatMessage chatMessage) {
            this.chatMessage = chatMessage;
            return this;
        }

        public AugmentationResultBuilder contents(List<Content> contents) {
            this.contents = contents;
            return this;
        }

        public AugmentationResult build() {
            return new AugmentationResult(this.chatMessage, this.contents);
        }
    }
}
