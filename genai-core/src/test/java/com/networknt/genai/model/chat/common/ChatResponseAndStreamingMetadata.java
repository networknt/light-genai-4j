package com.networknt.genai.model.chat.common;

import com.networknt.genai.model.chat.response.ChatResponse;

public record ChatResponseAndStreamingMetadata(ChatResponse chatResponse,
                                               StreamingMetadata streamingMetadata) {
}
