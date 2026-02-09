package com.networknt.agent.model.chat.common;

import com.networknt.agent.model.chat.response.CompleteToolCall;
import com.networknt.agent.model.chat.response.PartialToolCall;
import com.networknt.agent.model.chat.response.StreamingChatResponseHandler;
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
