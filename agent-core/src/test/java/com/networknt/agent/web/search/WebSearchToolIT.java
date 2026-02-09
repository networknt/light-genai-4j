package com.networknt.agent.web.search;

import com.networknt.agent.tool.ToolSpecification;
import com.networknt.agent.tool.ToolSpecifications;
import com.networknt.agent.data.message.AiMessage;
import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.model.chat.ChatModel;
import com.networknt.agent.model.chat.request.ChatRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class WebSearchToolIT {

    protected abstract WebSearchEngine searchEngine();

    protected abstract ChatModel chatModel();

    @Test
    void should_be_usable_tool_with_chatModel() {
        // given
        WebSearchTool webSearchTool = WebSearchTool.from(searchEngine());
        List<ToolSpecification> tools = ToolSpecifications.toolSpecificationsFrom(webSearchTool);

        UserMessage userMessage = UserMessage.from("What is LangChain4j project?");

        ChatRequest chatRequest = ChatRequest.builder()
                .messages(userMessage)
                .toolSpecifications(tools)
                .build();

        // when
        AiMessage aiMessage = chatModel().chat(chatRequest).aiMessage();

        // then
        assertThat(aiMessage.hasToolExecutionRequests()).isTrue();
        assertThat(aiMessage.toolExecutionRequests())
                .anySatisfy(toolSpec -> {
                            assertThat(toolSpec.name())
                                    .containsIgnoringCase("searchWeb");
                            assertThat(toolSpec.arguments())
                                    .isNotBlank();
                        }
                );
    }

    @Test
    void should_return_pretty_result_as_a_tool() {
        // given
        WebSearchTool webSearchTool = WebSearchTool.from(searchEngine());
        String searchTerm = "What is LangChain4j project?";

        // when
        String strResult = webSearchTool.searchWeb(searchTerm);

        // then
        assertThat(strResult).isNotBlank();
        assertThat(strResult)
                .as("At least the string result should be contains 'java' and 'AI' ignoring case")
                .containsIgnoringCase("Java")
                .containsIgnoringCase("AI");
    }
}
