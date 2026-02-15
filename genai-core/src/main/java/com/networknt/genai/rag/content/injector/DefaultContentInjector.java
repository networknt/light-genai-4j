package com.networknt.genai.rag.content.injector;

import static com.networknt.genai.internal.Utils.copy;
import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureNotEmpty;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static java.util.stream.Collectors.joining;

import com.networknt.genai.data.document.Metadata;
import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.TextContent;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.model.input.Prompt;
import com.networknt.genai.model.input.PromptTemplate;
import com.networknt.genai.rag.content.Content;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link ContentInjector} intended to be suitable for the majority of use cases.
 * <br>
 * <br>
 * It's important to note that while efforts will be made to avoid breaking changes,
 * the default behavior of this class may be updated in the future if it's found
 * that the current behavior does not adequately serve the majority of use cases.
 * Such changes would be made to benefit both current and future users.
 * <br>
 * <br>
 * This implementation appends all given {@link Content}s to the end of the given {@link UserMessage}
 * in their order of iteration.
 * Refer to {@link #DEFAULT_PROMPT_TEMPLATE} and implementation for more details.
 * <br>
 * <br>
 * Configurable parameters (optional):
 * <br>
 * - {@link #promptTemplate}: The prompt template that defines how the original {@code userMessage}
 * and {@code contents} are combined into the resulting {@link UserMessage}.
 * The text of the template should contain the {@code {{userMessage}}} and {@code {{contents}}} variables.
 * <br>
 * - {@link #metadataKeysToInclude}: A list of {@link Metadata} keys that should be included
 * with each {@link Content#textSegment()}.
 */
public class DefaultContentInjector implements ContentInjector {

    /**
     * The default prompt template.
     */
    public static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = PromptTemplate.from(
            """
                    {{userMessage}}

                    Answer using the following information:
                    {{contents}}""");

    private final PromptTemplate promptTemplate;
    /**
     * The metadata keys to include with each content.
     */
    protected final List<String> metadataKeysToInclude;

    /**
     * Creates a new default content injector.
     */
    public DefaultContentInjector() {
        this(DEFAULT_PROMPT_TEMPLATE, null);
    }

    /**
     * Creates a new default content injector.
     *
     * @param metadataKeysToInclude the metadata keys to include
     */
    public DefaultContentInjector(List<String> metadataKeysToInclude) {
        this(DEFAULT_PROMPT_TEMPLATE, ensureNotEmpty(metadataKeysToInclude, "metadataKeysToInclude"));
    }

    /**
     * Creates a new default content injector.
     *
     * @param promptTemplate the prompt template
     */
    public DefaultContentInjector(PromptTemplate promptTemplate) {
        this(ensureNotNull(promptTemplate, "promptTemplate"), null);
    }

    /**
     * Creates a new default content injector.
     *
     * @param promptTemplate        the prompt template
     * @param metadataKeysToInclude the metadata keys to include
     */
    public DefaultContentInjector(PromptTemplate promptTemplate, List<String> metadataKeysToInclude) {
        this.promptTemplate = getOrDefault(promptTemplate, DEFAULT_PROMPT_TEMPLATE);
        this.metadataKeysToInclude = copy(metadataKeysToInclude);
    }

    /**
     * Returns a new builder.
     *
     * @return a new builder
     */
    public static DefaultContentInjectorBuilder builder() {
        return new DefaultContentInjectorBuilder();
    }

    @Override
    public ChatMessage inject(List<Content> contents, ChatMessage chatMessage) {
        if (contents.isEmpty()) {
            return chatMessage;
        }

        Prompt prompt = createPrompt(chatMessage, contents);
        if (chatMessage instanceof UserMessage userMessage) {
            return userMessage.toBuilder()
                    .contents(List.of(TextContent.from(prompt.text())))
                    .build();
        } else {
            return prompt.toUserMessage();
        }
    }

    /**
     * Creates a prompt for the chat model.
     *
     * @param chatMessage the chat message
     * @param contents    the contents
     * @return the prompt
     */
    protected Prompt createPrompt(ChatMessage chatMessage, List<Content> contents) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("userMessage", ((UserMessage) chatMessage).singleText());
        variables.put("contents", format(contents));
        return promptTemplate.apply(variables);
    }

    /**
     * Formats the contents into a single string.
     *
     * @param contents the contents
     * @return the formatted contents
     */
    protected String format(List<Content> contents) {
        return contents.stream().map(this::format).collect(joining("\n\n"));
    }

    /**
     * Formats a single content.
     *
     * @param content the content
     * @return the formatted content
     */
    protected String format(Content content) {

        TextSegment segment = content.textSegment();

        if (metadataKeysToInclude.isEmpty()) {
            return segment.text();
        }

        String segmentContent = segment.text();
        String segmentMetadata = format(segment.metadata());

        return format(segmentContent, segmentMetadata);
    }

    /**
     * Formats the metadata.
     *
     * @param metadata the metadata
     * @return the formatted metadata
     */
    protected String format(Metadata metadata) {
        StringBuilder formattedMetadata = new StringBuilder();
        for (String metadataKey : metadataKeysToInclude) {
            String metadataValue = metadata.getString(metadataKey);
            if (metadataValue != null) {
                if (!formattedMetadata.isEmpty()) {
                    formattedMetadata.append("\n");
                }
                formattedMetadata.append(metadataKey).append(": ").append(metadataValue);
            }
        }
        return formattedMetadata.toString();
    }

    /**
     * Formats the content and metadata.
     *
     * @param segmentContent  the segment content
     * @param segmentMetadata the segment metadata
     * @return the formatted content and metadata
     */
    protected String format(String segmentContent, String segmentMetadata) {
        return segmentMetadata.isEmpty()
                ? segmentContent
                : String.format("content: %s\n%s", segmentContent, segmentMetadata);
    }

    /**
     * Builder for {@link DefaultContentInjector}.
     */
    public static class DefaultContentInjectorBuilder {

        private PromptTemplate promptTemplate;
        private List<String> metadataKeysToInclude;

        DefaultContentInjectorBuilder() {}

        /**
         * Sets the prompt template.
         *
         * @param promptTemplate the prompt template
         * @return the builder
         */
        public DefaultContentInjectorBuilder promptTemplate(PromptTemplate promptTemplate) {
            this.promptTemplate = promptTemplate;
            return this;
        }

        /**
         * Sets the metadata keys to include.
         *
         * @param metadataKeysToInclude the metadata keys to include
         * @return the builder
         */
        public DefaultContentInjectorBuilder metadataKeysToInclude(List<String> metadataKeysToInclude) {
            this.metadataKeysToInclude = metadataKeysToInclude;
            return this;
        }

        /**
         * Builds the default content injector.
         *
         * @return the default content injector
         */
        public DefaultContentInjector build() {
            return new DefaultContentInjector(this.promptTemplate, this.metadataKeysToInclude);
        }
    }
}
