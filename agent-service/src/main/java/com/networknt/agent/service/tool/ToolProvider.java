package com.networknt.agent.service.tool;

import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.invocation.InvocationParameters;
import com.networknt.agent.memory.ChatMemory;
import com.networknt.agent.service.MemoryId;

/**
 * A tool provider. It is called each time the AI service is called and supplies tools for that specific call.
 * <p>
 * Tools returned in {@link ToolProviderResult} will be included in the request to the LLM.
 **/
@FunctionalInterface
public interface ToolProvider {

    /**
     * Provides tools for the request to the LLM.
     *
     * @param request the {@link ToolProviderRequest}, contains {@link UserMessage},
     *                {@link ChatMemory} ID (see {@link MemoryId}) and {@link InvocationParameters}.
     * @return {@link ToolProviderResult} contains tools that should be included in the request to the LLM.
     */
    ToolProviderResult provideTools(ToolProviderRequest request);
}
