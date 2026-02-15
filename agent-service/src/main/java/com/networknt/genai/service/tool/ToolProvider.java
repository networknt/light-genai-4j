package com.networknt.genai.service.tool;

import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.invocation.InvocationParameters;
import com.networknt.genai.memory.ChatMemory;
import com.networknt.genai.service.MemoryId;

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
