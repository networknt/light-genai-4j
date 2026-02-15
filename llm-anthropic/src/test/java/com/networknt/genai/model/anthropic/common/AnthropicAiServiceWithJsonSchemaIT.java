package com.networknt.genai.model.anthropic.common;

import static com.networknt.genai.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

import com.networknt.genai.model.anthropic.AnthropicChatModel;
import com.networknt.genai.model.anthropic.AnthropicChatModelName;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.service.common.AbstractAiServiceWithJsonSchemaIT;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
public class AnthropicAiServiceWithJsonSchemaIT extends AbstractAiServiceWithJsonSchemaIT {

    @Override
    protected List<ChatModel> models() {
        return List.of(
                AnthropicChatModel.builder()
                        .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                        .modelName(AnthropicChatModelName.CLAUDE_HAIKU_4_5_20251001)
                        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
                        .logRequests(false)
                        .logRequests(true)
                        .build(),
                AnthropicChatModel.builder()
                        .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                        .beta("structured-outputs-2025-11-13") // testing backward compatibility
                        .modelName(AnthropicChatModelName.CLAUDE_HAIKU_4_5_20251001)
                        .supportedCapabilities(RESPONSE_FORMAT_JSON_SCHEMA)
                        .logRequests(false)
                        .logRequests(true)
                        .build()
        );
    }

    @Disabled("Claude cannot do it properly.")
    @Override
    @ParameterizedTest
    @MethodSource("models")
    protected void should_extract_pojo_with_local_date_time_fields(ChatModel model) {}
}
