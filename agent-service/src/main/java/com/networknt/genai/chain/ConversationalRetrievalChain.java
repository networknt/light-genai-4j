package com.networknt.genai.chain;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.memory.ChatMemory;
import com.networknt.genai.memory.chat.MessageWindowChatMemory;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.rag.AugmentationRequest;
import com.networknt.genai.rag.AugmentationResult;
import com.networknt.genai.rag.DefaultRetrievalAugmentor;
import com.networknt.genai.rag.RetrievalAugmentor;
import com.networknt.genai.rag.content.retriever.ContentRetriever;
import com.networknt.genai.rag.query.Metadata;
import com.networknt.genai.service.AiServices;

import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * A chain for conversing with a specified {@link ChatModel}
 * based on the information retrieved by a specified {@link ContentRetriever}.
 * Includes a default {@link ChatMemory} (a message window with maximum 10 messages), which can be overridden.
 * You can fully customize RAG behavior by providing an instance of a {@link RetrievalAugmentor},
 * such as {@link DefaultRetrievalAugmentor}, or your own custom implementation.
 * <br>
 * Chains are not going to be developed further, it is recommended to use {@link AiServices} instead.
 */
public class ConversationalRetrievalChain implements Chain<String, String> {

    private final ChatModel chatModel;
    private final ChatMemory chatMemory;
    private final RetrievalAugmentor retrievalAugmentor;

    public ConversationalRetrievalChain(ChatModel chatModel,
                                        ChatMemory chatMemory,
                                        ContentRetriever contentRetriever) {
        this(
                chatModel,
                chatMemory,
                DefaultRetrievalAugmentor.builder()
                        .contentRetriever(contentRetriever)
                        .build()
        );
    }

    public ConversationalRetrievalChain(ChatModel chatModel,
                                        ChatMemory chatMemory,
                                        RetrievalAugmentor retrievalAugmentor) {
        this.chatModel = ensureNotNull(chatModel, "chatModel");
        this.chatMemory = getOrDefault(chatMemory, () -> MessageWindowChatMemory.withMaxMessages(10));
        this.retrievalAugmentor = ensureNotNull(retrievalAugmentor, "retrievalAugmentor");
    }

    @Override
    public String execute(String query) {

        UserMessage userMessage = UserMessage.from(query);
        userMessage = augment(userMessage);
        chatMemory.add(userMessage);

        AiMessage aiMessage = chatModel.chat(chatMemory.messages()).aiMessage();

        chatMemory.add(aiMessage);
        return aiMessage.text();
    }

    private UserMessage augment(UserMessage userMessage) {
        Metadata metadata = Metadata.from(userMessage, chatMemory.id(), chatMemory.messages());

        AugmentationRequest augmentationRequest = new AugmentationRequest(userMessage, metadata);

        AugmentationResult augmentationResult = retrievalAugmentor.augment(augmentationRequest);

        return (UserMessage) augmentationResult.chatMessage();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ChatModel chatModel;
        private ChatMemory chatMemory;
        private RetrievalAugmentor retrievalAugmentor;

        public Builder chatModel(ChatModel chatModel) {
            this.chatModel = chatModel;
            return this;
        }

        public Builder chatMemory(ChatMemory chatMemory) {
            this.chatMemory = chatMemory;
            return this;
        }

        public Builder contentRetriever(ContentRetriever contentRetriever) {
            if (contentRetriever != null) {
                this.retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                        .contentRetriever(contentRetriever)
                        .build();
            }
            return this;
        }

        public Builder retrievalAugmentor(RetrievalAugmentor retrievalAugmentor) {
            this.retrievalAugmentor = retrievalAugmentor;
            return this;
        }

        public ConversationalRetrievalChain build() {
            return new ConversationalRetrievalChain(chatModel, chatMemory, retrievalAugmentor);
        }
    }
}
