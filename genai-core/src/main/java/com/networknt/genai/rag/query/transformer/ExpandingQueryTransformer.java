package com.networknt.genai.rag.query.transformer;

import com.networknt.genai.internal.Utils;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.input.Prompt;
import com.networknt.genai.model.input.PromptTemplate;
import com.networknt.genai.rag.query.Query;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureGreaterThanZero;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * A {@link QueryTransformer} that utilizes a {@link ChatModel} to expand a given {@link Query}.
 * <br>
 * Refer to {@link #DEFAULT_PROMPT_TEMPLATE} and implementation for more details.
 * <br>
 * <br>
 * Configurable parameters (optional):
 * <br>
 * - {@link #promptTemplate}: The prompt template used to instruct the LLM to expand the provided {@link Query}.
 * <br>
 * - {@link #n}: The number of {@link Query}s to generate. Default value is 3.
 *
 * @see DefaultQueryTransformer
 * @see CompressingQueryTransformer
 */
public class ExpandingQueryTransformer implements QueryTransformer {

    /**
     * The default prompt template used for query expansion.
     */
    public static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = PromptTemplate.from(
            """
                    Generate {{n}} different versions of a provided user query. \
                    Each version should be worded differently, using synonyms or alternative sentence structures, \
                    but they should all retain the original meaning. \
                    These versions will be used to retrieve relevant documents. \
                    It is very important to provide each query version on a separate line, \
                    without enumerations, hyphens, or any additional formatting!
                    User query: {{query}}"""
    );
    /**
     * The default number of queries to generate.
     */
    public static final int DEFAULT_N = 3;

    /**
     * The chat model used for query expansion.
     */
    protected final ChatModel chatModel;

    /**
     * The prompt template used for query expansion.
     */
    protected final PromptTemplate promptTemplate;

    /**
     * The number of queries to generate.
     */
    protected final int n;

    /**
     * Creates a new expanding query transformer.
     *
     * @param chatModel the chat model
     */
    public ExpandingQueryTransformer(ChatModel chatModel) {
        this(chatModel, DEFAULT_PROMPT_TEMPLATE, DEFAULT_N);
    }

    /**
     * Creates a new expanding query transformer.
     *
     * @param chatModel the chat model
     * @param n         the number of queries to generate
     */
    public ExpandingQueryTransformer(ChatModel chatModel, int n) {
        this(chatModel, DEFAULT_PROMPT_TEMPLATE, n);
    }

    /**
     * Creates a new expanding query transformer.
     *
     * @param chatModel      the chat model
     * @param promptTemplate the prompt template
     */
    public ExpandingQueryTransformer(ChatModel chatModel, PromptTemplate promptTemplate) {
        this(chatModel, ensureNotNull(promptTemplate, "promptTemplate"), DEFAULT_N);
    }

    /**
     * Creates a new expanding query transformer.
     *
     * @param chatModel      the chat model
     * @param promptTemplate the prompt template
     * @param n              the number of queries to generate
     */
    public ExpandingQueryTransformer(ChatModel chatModel, PromptTemplate promptTemplate, Integer n) {
        this.chatModel = ensureNotNull(chatModel, "chatModel");
        this.promptTemplate = getOrDefault(promptTemplate, DEFAULT_PROMPT_TEMPLATE);
        this.n = ensureGreaterThanZero(getOrDefault(n, DEFAULT_N), "n");
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    public static ExpandingQueryTransformerBuilder builder() {
        return new ExpandingQueryTransformerBuilder();
    }

    @Override
    public Collection<Query> transform(Query query) {
        Prompt prompt = createPrompt(query);
        String response = chatModel.chat(prompt.text());
        List<String> queries = parse(response);
        return queries.stream()
                .map(queryText -> query.metadata() == null
                        ? Query.from(queryText)
                        : Query.from(queryText, query.metadata()))
                .collect(toList());
    }

    /**
     * Creates a prompt for the chat model.
     *
     * @param query the query
     * @return the prompt
     */
    protected Prompt createPrompt(Query query) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("query", query.text());
        variables.put("n", n);
        return promptTemplate.apply(variables);
    }

    /**
     * Parses the response from the chat model.
     *
     * @param queries the response from the chat model
     * @return the list of queries
     */
    protected List<String> parse(String queries) {
        return stream(queries.split("\n"))
                .filter(Utils::isNotNullOrBlank)
                .collect(toList());
    }

    /**
     * Builder for {@link ExpandingQueryTransformer}.
     */
    public static class ExpandingQueryTransformerBuilder {
        private ChatModel chatModel;
        private PromptTemplate promptTemplate;
        private Integer n;

        ExpandingQueryTransformerBuilder() {
        }

        /**
         * Sets the chat model.
         *
         * @param chatModel the chat model
         * @return the builder
         */
        public ExpandingQueryTransformerBuilder chatModel(ChatModel chatModel) {
            this.chatModel = chatModel;
            return this;
        }

        /**
         * Sets the prompt template.
         *
         * @param promptTemplate the prompt template
         * @return the builder
         */
        public ExpandingQueryTransformerBuilder promptTemplate(PromptTemplate promptTemplate) {
            this.promptTemplate = promptTemplate;
            return this;
        }

        /**
         * Sets the number of queries to generate.
         *
         * @param n the number of queries
         * @return the builder
         */
        public ExpandingQueryTransformerBuilder n(Integer n) {
            this.n = n;
            return this;
        }

        /**
         * Builds the expanding query transformer.
         *
         * @return the expanding query transformer
         */
        public ExpandingQueryTransformer build() {
            return new ExpandingQueryTransformer(this.chatModel, this.promptTemplate, this.n);
        }
    }
}
