package com.networknt.genai.chain;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.memory.ChatMemory;
import com.networknt.genai.memory.chat.MessageWindowChatMemory;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.service.AiServices;

import static com.networknt.genai.data.message.UserMessage.userMessage;
import static com.networknt.genai.internal.ValidationUtils.ensureNotBlank;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * A chain for conversing with a specified {@link ChatModel} while maintaining a memory of the conversation.
 * Includes a default {@link ChatMemory} (a message window with maximum 10 messages), which can be overridden.
 * <br>
 * Chains are not going to be developed further, it is recommended to use {@link AiServices} instead.
 */
public class ConversationalChain implements Chain<String, String> {

    private final ChatModel chatModel;
    private final ChatMemory chatMemory;

    private ConversationalChain(ChatModel chatModel, ChatMemory chatMemory) {
        this.chatModel = ensureNotNull(chatModel, "chatModel");
        this.chatMemory = chatMemory == null ? MessageWindowChatMemory.withMaxMessages(10) : chatMemory;
    }

    public static ConversationalChainBuilder builder() {
        return new ConversationalChainBuilder();
    }

    @Override
    public String execute(String userMessage) {

        chatMemory.add(userMessage(ensureNotBlank(userMessage, "userMessage")));

        AiMessage aiMessage = chatModel.chat(chatMemory.messages()).aiMessage();

        chatMemory.add(aiMessage);

        return aiMessage.text();
    }

    public static class ConversationalChainBuilder {
        private ChatModel chatModel;
        private ChatMemory chatMemory;

        ConversationalChainBuilder() {
        }

        public ConversationalChainBuilder chatModel(ChatModel chatModel) {
            this.chatModel = chatModel;
            return this;
        }

        public ConversationalChainBuilder chatMemory(ChatMemory chatMemory) {
            this.chatMemory = chatMemory;
            return this;
        }

        public ConversationalChain build() {
            return new ConversationalChain(this.chatModel, this.chatMemory);
        }
    }
}
