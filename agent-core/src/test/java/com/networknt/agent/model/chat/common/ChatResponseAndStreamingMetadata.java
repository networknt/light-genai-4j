package com.networknt.agent.model.chat.common;

import com.networknt.agent.model.chat.response.ChatResponse;

public record ChatResponseAndStreamingMetadata(ChatResponse chatResponse,
                                               StreamingMetadata streamingMetadata) {
}
