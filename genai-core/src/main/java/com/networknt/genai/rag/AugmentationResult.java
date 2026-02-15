package com.networknt.genai.rag;

import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.rag.content.Content;

import java.util.List;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

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

    /**
     * Creates a new augmentation result.
     *
     * @param chatMessage the augmented chat message
     * @param contents    the contents used for augmentation
     */
    public AugmentationResult(ChatMessage chatMessage, List<Content> contents) {
        this.chatMessage = ensureNotNull(chatMessage, "chatMessage");
        this.contents = copy(contents);
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    public static AugmentationResultBuilder builder() {
        return new AugmentationResultBuilder();
    }

    /**
     * @return the augmented chat message
     */
    public ChatMessage chatMessage() {
        return chatMessage;
    }

    /**
     * @return the contents used for augmentation
     */
    public List<Content> contents() {
        return contents;
    }

    /**
     * Builder for {@link AugmentationResult}.
     */
    public static class AugmentationResultBuilder {

        private ChatMessage chatMessage;
        private List<Content> contents;

        AugmentationResultBuilder() {
        }

        /**
         * Sets the chat message.
         *
         * @param chatMessage the chat message
         * @return the builder
         */
        public AugmentationResultBuilder chatMessage(ChatMessage chatMessage) {
            this.chatMessage = chatMessage;
            return this;
        }

        /**
         * Sets the contents.
         *
         * @param contents the contents
         * @return the builder
         */
        public AugmentationResultBuilder contents(List<Content> contents) {
            this.contents = contents;
            return this;
        }

        /**
         * Builds the augmentation result.
         *
         * @return the augmentation result
         */
        public AugmentationResult build() {
            return new AugmentationResult(this.chatMessage, this.contents);
        }
    }
}
