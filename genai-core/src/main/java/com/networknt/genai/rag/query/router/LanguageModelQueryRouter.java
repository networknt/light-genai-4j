package com.networknt.genai.rag.query.router;

import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.input.Prompt;
import com.networknt.genai.model.input.PromptTemplate;
import com.networknt.genai.rag.content.retriever.ContentRetriever;
import com.networknt.genai.rag.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureNotBlank;
import static com.networknt.genai.internal.ValidationUtils.ensureNotEmpty;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static com.networknt.genai.rag.query.router.LanguageModelQueryRouter.FallbackStrategy.DO_NOT_ROUTE;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * A {@link QueryRouter} that utilizes a {@link ChatModel} to make a routing decision.
 * <br>
 * Each {@link ContentRetriever} provided in the constructor should be accompanied by a description which
 * should help the LLM to decide where to route a {@link Query}.
 * <br>
 * Refer to {@link #DEFAULT_PROMPT_TEMPLATE} and implementation for more details.
 * <br>
 * <br>
 * Configurable parameters (optional):
 * <br>
 * - {@link #promptTemplate}: The prompt template used to ask the LLM for routing decisions.
 * <br>
 * - {@link #fallbackStrategy}: The strategy applied if the call to the LLM fails of if LLM does not return a valid response.
 * Please check {@link FallbackStrategy} for more details. Default value: {@link FallbackStrategy#DO_NOT_ROUTE}
 *
 * @see DefaultQueryRouter
 */
public class LanguageModelQueryRouter implements QueryRouter {

    /**
     * The default prompt template for routing decisions.
     */
    public static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = PromptTemplate.from(
            """
                    Based on the user query, determine the most suitable data source(s) \
                    to retrieve relevant information from the following options:
                    {{options}}
                    It is very important that your answer consists of either a single number \
                    or multiple numbers separated by commas and nothing else!
                    User query: {{query}}"""
    );

    /**
     * The chat model used for routing decisions.
     */
    protected final ChatModel chatModel;
    /**
     * The prompt template.
     */
    protected final PromptTemplate promptTemplate;
    /**
     * The options string containing descriptions of content retrievers.
     */
    protected final String options;
    /**
     * The map of IDs to content retrievers.
     */
    protected final Map<Integer, ContentRetriever> idToRetriever;
    /**
     * The fallback strategy.
     */
    protected final FallbackStrategy fallbackStrategy;

    /**
     * Creates a new instance.
     *
     * @param chatModel             the chat model
     * @param retrieverToDescription the map of content retrievers to their descriptions
     */
    public LanguageModelQueryRouter(ChatModel chatModel,
                                    Map<ContentRetriever, String> retrieverToDescription) {
        this(chatModel, retrieverToDescription, DEFAULT_PROMPT_TEMPLATE, DO_NOT_ROUTE);
    }

    /**
     * Creates a new instance.
     *
     * @param chatModel             the chat model
     * @param retrieverToDescription the map of content retrievers to their descriptions
     * @param promptTemplate        the prompt template
     * @param fallbackStrategy      the fallback strategy
     */
    public LanguageModelQueryRouter(ChatModel chatModel,
                                    Map<ContentRetriever, String> retrieverToDescription,
                                    PromptTemplate promptTemplate,
                                    FallbackStrategy fallbackStrategy) {
        this.chatModel = ensureNotNull(chatModel, "chatModel");
        ensureNotEmpty(retrieverToDescription, "retrieverToDescription");
        this.promptTemplate = getOrDefault(promptTemplate, DEFAULT_PROMPT_TEMPLATE);

        Map<Integer, ContentRetriever> idToRetriever = new HashMap<>();
        StringBuilder optionsBuilder = new StringBuilder();
        int id = 1;
        for (Map.Entry<ContentRetriever, String> entry : retrieverToDescription.entrySet()) {
            idToRetriever.put(id, ensureNotNull(entry.getKey(), "ContentRetriever"));

            if (id > 1) {
                optionsBuilder.append("\n");
            }
            optionsBuilder.append(id);
            optionsBuilder.append(": ");
            optionsBuilder.append(ensureNotBlank(entry.getValue(), "ContentRetriever description"));

            id++;
        }
        this.idToRetriever = idToRetriever;
        this.options = optionsBuilder.toString();
        this.fallbackStrategy = getOrDefault(fallbackStrategy, DO_NOT_ROUTE);
    }

    /**
     * Creates a new builder.
     *
     * @return the builder
     */
    public static LanguageModelQueryRouterBuilder builder() {
        return new LanguageModelQueryRouterBuilder();
    }

    @Override
    public Collection<ContentRetriever> route(Query query) {
        Prompt prompt = createPrompt(query);
        try {
            String response = chatModel.chat(prompt.text());
            return parse(response);
        } catch (Exception e) {
            return fallback(query, e);
        }
    }

    /**
     * Handles fallback when routing fails.
     *
     * @param query the query
     * @param e the exception
     * @return the collection of content retrievers
     */
    protected Collection<ContentRetriever> fallback(Query query, Exception e) {
        return switch (fallbackStrategy) {
            case DO_NOT_ROUTE -> {
                yield emptyList();
            }
            case ROUTE_TO_ALL -> {
                yield new ArrayList<>(idToRetriever.values());
            }
            default -> throw new RuntimeException(e);
        };
    }

    /**
     * Creates a prompt for the given query.
     *
     * @param query the query
     * @return the prompt
     */
    protected Prompt createPrompt(Query query) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("query", query.text());
        variables.put("options", options);
        return promptTemplate.apply(variables);
    }

    /**
     * Parses the routing choices returned by the LLM.
     *
     * @param choices the choices
     * @return the collection of content retrievers
     */
    protected Collection<ContentRetriever> parse(String choices) {
        return stream(choices.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .map(idToRetriever::get)
                .collect(toList());
    }

    /**
     * Strategy applied if the call to the LLM fails of if LLM does not return a valid response.
     * It could be because it was formatted improperly, or it is unclear where to route.
     */
    public enum FallbackStrategy {

        /**
         * In this case, the {@link Query} will not be routed to any {@link ContentRetriever},
         * thus skipping the RAG flow. No content will be appended to the original {@link UserMessage}.
         */
        DO_NOT_ROUTE,

        /**
         * In this case, the {@link Query} will be routed to all {@link ContentRetriever}s.
         */
        ROUTE_TO_ALL,

        /**
         * In this case, an original exception will be re-thrown, and the RAG flow will fail.
         */
        FAIL
    }

    /**
     * Builder for {@link LanguageModelQueryRouter}.
     */
    public static class LanguageModelQueryRouterBuilder {
        private ChatModel chatModel;
        private Map<ContentRetriever, String> retrieverToDescription;
        private PromptTemplate promptTemplate;
        private FallbackStrategy fallbackStrategy;

        LanguageModelQueryRouterBuilder() {
        }

        /**
         * Sets the chat model.
         *
         * @param chatModel the chat model
         * @return the builder
         */
        public LanguageModelQueryRouterBuilder chatModel(ChatModel chatModel) {
            this.chatModel = chatModel;
            return this;
        }

        /**
         * Sets the map of content retrievers to their descriptions.
         *
         * @param retrieverToDescription the map of content retrievers to their descriptions
         * @return the builder
         */
        public LanguageModelQueryRouterBuilder retrieverToDescription(Map<ContentRetriever, String> retrieverToDescription) {
            this.retrieverToDescription = retrieverToDescription;
            return this;
        }

        /**
         * Sets the prompt template.
         *
         * @param promptTemplate the prompt template
         * @return the builder
         */
        public LanguageModelQueryRouterBuilder promptTemplate(PromptTemplate promptTemplate) {
            this.promptTemplate = promptTemplate;
            return this;
        }

        /**
         * Sets the fallback strategy.
         *
         * @param fallbackStrategy the fallback strategy
         * @return the builder
         */
        public LanguageModelQueryRouterBuilder fallbackStrategy(FallbackStrategy fallbackStrategy) {
            this.fallbackStrategy = fallbackStrategy;
            return this;
        }

        /**
         * Builds the router.
         *
         * @return the router
         */
        public LanguageModelQueryRouter build() {
            return new LanguageModelQueryRouter(this.chatModel, this.retrieverToDescription, this.promptTemplate, this.fallbackStrategy);
        }
    }
}
