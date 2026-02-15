package com.networknt.genai.model.chat.common;

import com.networknt.genai.model.chat.response.CompleteToolCall;
import com.networknt.genai.model.chat.response.PartialToolCall;
import com.networknt.genai.model.chat.response.StreamingChatResponseHandler;
import java.util.List;
import java.util.Set;

public record StreamingMetadata(
        String concatenatedPartialResponses,
        int timesOnPartialResponseWasCalled,
        int timesOnPartialThinkingWasCalled,
        List<PartialToolCall> partialToolCalls,
        List<CompleteToolCall> completeToolCalls,
        int timesOnCompleteResponseWasCalled,
        Set<Thread> threads,
        StreamingChatResponseHandler handler) {}
