package com.networknt.genai.rag.query.transformer;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import com.networknt.genai.tool.ToolExecutionRequest;
import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.SystemMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.mock.ChatModelMock;
import com.networknt.genai.model.input.PromptTemplate;
import com.networknt.genai.rag.query.Metadata;
import com.networknt.genai.rag.query.Query;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;

class CompressingQueryTransformerTest {

    @Test
    void should_compress_query_and_chat_memory_into_single_query() {

        // given
        SystemMessage systemMessage = SystemMessage.from("Be polite");
        List<ChatMessage> chatMemory = asList(
                systemMessage, // this message will be ignored
                UserMessage.from("Tell me about Klaus Heisler"),
                AiMessage.from("He is a cool guy"),
                AiMessage.from(ToolExecutionRequest.builder() // this message will be ignored
                        .id("12345")
                        .name("current_time")
                        .arguments("{}")
                        .build()));

        UserMessage userMessage = UserMessage.from("How old is he?");
        Metadata metadata = Metadata.from(userMessage, systemMessage, "default", chatMemory);

        Query query = Query.from(userMessage.singleText(), metadata);

        String expectedCompressedQuery = "How old is Klaus Heisler?";

        ChatModelMock model = ChatModelMock.thatAlwaysResponds(expectedCompressedQuery);
        CompressingQueryTransformer transformer = new CompressingQueryTransformer(model);

        // when
        Collection<Query> queries = transformer.transform(query);

        // then
        assertThat(queries).containsExactly(Query.from(expectedCompressedQuery, metadata));

        assertThat(model.userMessageText())
                .isEqualTo(
                        """
                Read and understand the conversation between the User and the AI. \
                Then, analyze the new query from the User. \
                Identify all relevant details, terms, and context from both the conversation \
                and the new query. Reformulate this query into a clear, concise, \
                and self-contained format suitable for information retrieval.

                Conversation:
                User: Tell me about Klaus Heisler
                AI: He is a cool guy

                User query: How old is he?

                It is very important that you provide only reformulated query and nothing else! \
                Do not prepend a query with anything!""");
    }

    @Test
    void should_not_compress_when_empty_chat_memory() {

        // given
        List<ChatMessage> chatMemory = emptyList();

        SystemMessage systemMessage = SystemMessage.from("Be polite");
        UserMessage userMessage = UserMessage.from("Hello");
        Metadata metadata = Metadata.from(userMessage, systemMessage, "default", chatMemory);

        Query query = Query.from(userMessage.singleText(), metadata);

        ChatModel model = mock(ChatModel.class);
        CompressingQueryTransformer transformer = new CompressingQueryTransformer(model);

        // when
        Collection<Query> queries = transformer.transform(query);

        // then
        assertThat(queries).containsExactly(query);

        verifyNoInteractions(model);
    }

    @Test
    void should_compress_query_and_chat_memory_into_single_query_using_custom_prompt_template() {

        // given
        PromptTemplate promptTemplate = PromptTemplate.from(
                "Given the following conversation: {{chatMemory}} reformulate the following query: {{query}}");

        List<ChatMessage> chatMemory =
                asList(UserMessage.from("Tell me about Klaus Heisler"), AiMessage.from("He is a cool guy"));
        SystemMessage systemMessage = SystemMessage.from("Be polite");
        UserMessage userMessage = UserMessage.from("How old is he?");
        Metadata metadata = Metadata.from(userMessage, systemMessage, "default", chatMemory);
        Query query = Query.from(userMessage.singleText(), metadata);

        String expectedCompressedQuery = "How old is Klaus Heisler?";
        ChatModelMock model = ChatModelMock.thatAlwaysResponds(expectedCompressedQuery);

        CompressingQueryTransformer transformer = new CompressingQueryTransformer(model, promptTemplate);

        // when
        Collection<Query> queries = transformer.transform(query);

        // then
        assertThat(queries).containsExactly(Query.from(expectedCompressedQuery, metadata));

        assertThat(model.userMessageText())
                .isEqualTo(
                        """
                Given the following conversation: \
                User: Tell me about Klaus Heisler
                AI: He is a cool guy \
                reformulate the following query: How old is he?""");
    }

    @Test
    void should_compress_query_and_chat_memory_into_single_query_using_custom_prompt_template_builder() {

        // given
        PromptTemplate promptTemplate = PromptTemplate.from(
                "Given the following conversation: {{chatMemory}} reformulate the following query: {{query}}");

        SystemMessage systemMessage = SystemMessage.from("Be polite");
        List<ChatMessage> chatMemory = asList(
                systemMessage, UserMessage.from("Tell me about Klaus Heisler"), AiMessage.from("He is a cool guy"));
        UserMessage userMessage = UserMessage.from("How old is he?");
        Metadata metadata = Metadata.from(userMessage, systemMessage, "default", chatMemory);
        Query query = Query.from(userMessage.singleText(), metadata);

        String expectedCompressedQuery = "How old is Klaus Heisler?";
        ChatModelMock model = ChatModelMock.thatAlwaysResponds(expectedCompressedQuery);

        CompressingQueryTransformer transformer = CompressingQueryTransformer.builder()
                .chatModel(model)
                .promptTemplate(promptTemplate)
                .build();

        // when
        Collection<Query> queries = transformer.transform(query);

        // then
        assertThat(queries).containsExactly(Query.from(expectedCompressedQuery, metadata));

        assertThat(model.userMessageText())
                .isEqualTo(
                        """
                Given the following conversation: \
                User: Tell me about Klaus Heisler
                AI: He is a cool guy \
                reformulate the following query: How old is he?""");
    }
}
