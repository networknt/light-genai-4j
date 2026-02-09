package com.networknt.agent.rag.query.transformer;

import static com.networknt.agent.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.networknt.agent.data.message.AiMessage;
import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.data.message.SystemMessage;
import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.model.chat.ChatModel;
import com.networknt.agent.model.openai.OpenAiChatModel;
import com.networknt.agent.rag.query.Metadata;
import com.networknt.agent.rag.query.Query;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class CompressingQueryTransformerIT {

    @ParameterizedTest
    @MethodSource("models")
    void should_compress_query_and_chat_memory_into_single_query(ChatModel model) {

        // given
        List<ChatMessage> chatMemory =
                asList(UserMessage.from("Tell me about Klaus Heisler"), AiMessage.from("He is a cool guy"));

        SystemMessage systemMessage = SystemMessage.from("Be polite");
        UserMessage userMessage = UserMessage.from("How old is he?");
        Metadata metadata = Metadata.from(userMessage, systemMessage, "default", chatMemory);

        Query query = Query.from(userMessage.singleText(), metadata);

        CompressingQueryTransformer transformer = new CompressingQueryTransformer(model);

        // when
        Collection<Query> queries = transformer.transform(query);

        // then
        assertThat(queries).hasSize(1);

        Query compressedQuery = queries.iterator().next();
        assertThat(compressedQuery.text()).contains("Klaus");
        assertThat(compressedQuery.text()).doesNotContain(":");
    }

    static Stream<Arguments> models() {
        return Stream.of(
                Arguments.of(OpenAiChatModel.builder()
                        .baseUrl(System.getenv("OPENAI_BASE_URL"))
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
                        .modelName(GPT_4_O_MINI)
                        .logRequests(true)
                        .logResponses(true)
                        .build())
                // TODO add more models
                );
    }
}
