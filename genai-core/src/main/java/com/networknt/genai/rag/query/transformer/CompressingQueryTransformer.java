package com.networknt.genai.rag.query.transformer;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.memory.ChatMemory;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.input.Prompt;
import com.networknt.genai.model.input.PromptTemplate;
import com.networknt.genai.rag.query.Query;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

/**
 * A {@link QueryTransformer} that leverages a {@link ChatModel} to condense a given {@link Query}
 * along with a chat memory (previous conversation history) into a concise {@link Query}.
 * This is applicable only when a {@link ChatMemory} is in use.
 * Refer to {@link #DEFAULT_PROMPT_TEMPLATE} and implementation for more details.
 * <br>
 * <br>
 * Configurable parameters (optional):
 * <br>
 * - {@link #promptTemplate}: The prompt template used to instruct the LLM to compress the specified {@link Query}.
 *
 * @see DefaultQueryTransformer
 * @see ExpandingQueryTransformer
 */
public class CompressingQueryTransformer implements QueryTransformer {

    /**
     * The default prompt template used for query compression.
     */
    public static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = PromptTemplate.from(
            """
                    Read and understand the conversation between the User and the AI. \
                    Then, analyze the new query from the User. \
                    Identify all relevant details, terms, and context from both the conversation and the new query. \
                    Reformulate this query into a clear, concise, and self-contained format suitable for information retrieval.
                    
                    Conversation:
                    {{chatMemory}}
                    
                    User query: {{query}}
                    
                    It is very important that you provide only reformulated query and nothing else! \
                    Do not prepend a query with anything!"""
    );

    /**
     * The prompt template used for query compression.
     */
    protected final PromptTemplate promptTemplate;

    /**
     * The chat model used for query compression.
     */
    protected final ChatModel chatModel;

    /**
     * Creates a new compressing query transformer with the given chat model.
     *
     * @param chatModel the chat model
     */
    public CompressingQueryTransformer(ChatModel chatModel) {
        this(chatModel, DEFAULT_PROMPT_TEMPLATE);
    }

    /**
     * Creates a new compressing query transformer.
     *
     * @param chatModel      the chat model
     * @param promptTemplate the prompt template
     */
    public CompressingQueryTransformer(ChatModel chatModel, PromptTemplate promptTemplate) {
        this.chatModel = ensureNotNull(chatModel, "chatModel");
        this.promptTemplate = getOrDefault(promptTemplate, DEFAULT_PROMPT_TEMPLATE);
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    public static CompressingQueryTransformerBuilder builder() {
        return new CompressingQueryTransformerBuilder();
    }

    @Override
    public Collection<Query> transform(Query query) {

        List<ChatMessage> chatMemory = query.metadata().chatMemory();
        if (chatMemory == null || chatMemory.isEmpty()) {
            // no need to compress if there are no previous messages
            return singletonList(query);
        }

        Prompt prompt = createPrompt(query, format(chatMemory));
        String compressedQueryText = chatModel.chat(prompt.text());
        Query compressedQuery = query.metadata() == null
                ? Query.from(compressedQueryText)
                : Query.from(compressedQueryText, query.metadata());
        return singletonList(compressedQuery);
    }

    /**
     * Formats the chat memory into a single string.
     *
     * @param chatMemory the chat memory
     * @return the formatted chat memory
     */
    protected String format(List<ChatMessage> chatMemory) {
        return chatMemory.stream()
                .map(this::format)
                .filter(Objects::nonNull)
                .collect(joining("\n"));
    }

    /**
     * Formats a single chat message.
     *
     * @param message the chat message
     * @return the formatted chat message
     */
    protected String format(ChatMessage message) {
        if (message instanceof UserMessage userMessage) {
            return "User: " + userMessage.singleText();
        } else if (message instanceof AiMessage aiMessage) {
            if (aiMessage.hasToolExecutionRequests()) {
                return null;
            }
            return "AI: " + aiMessage.text();
        } else {
            return null;
        }
    }

    /**
     * Creates a prompt for the chat model.
     *
     * @param query      the query
     * @param chatMemory the formatted chat memory
     * @return the prompt
     */
    protected Prompt createPrompt(Query query, String chatMemory) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("query", query.text());
        variables.put("chatMemory", chatMemory);
        return promptTemplate.apply(variables);
    }

    /**
     * Builder for {@link CompressingQueryTransformer}.
     */
    public static class CompressingQueryTransformerBuilder {
        private ChatModel chatModel;
        private PromptTemplate promptTemplate;

        CompressingQueryTransformerBuilder() {
        }

        /**
         * Sets the chat model.
         *
         * @param chatModel the chat model
         * @return the builder
         */
        public CompressingQueryTransformerBuilder chatModel(ChatModel chatModel) {
            this.chatModel = chatModel;
            return this;
        }

        /**
         * Sets the prompt template.
         *
         * @param promptTemplate the prompt template
         * @return the builder
         */
        public CompressingQueryTransformerBuilder promptTemplate(PromptTemplate promptTemplate) {
            this.promptTemplate = promptTemplate;
            return this;
        }

        /**
         * Builds the compressing query transformer.
         *
         * @return the compressing query transformer
         */
        public CompressingQueryTransformer build() {
            return new CompressingQueryTransformer(this.chatModel, this.promptTemplate);
        }
    }
}
