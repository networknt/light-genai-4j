package com.networknt.agent.memory.chat;

import com.networknt.agent.memory.ChatMemory;
import com.networknt.agent.service.MemoryId;

/**
 * Provides instances of {@link ChatMemory}.
 * Intended to be used with {@link com.networknt.agent.service.AiServices}.
 */
@FunctionalInterface
public interface ChatMemoryProvider {

    /**
     * Provides an instance of {@link ChatMemory}.
     * This method is called each time an AI Service method (having a parameter annotated with {@link MemoryId})
     * is called with a previously unseen memory ID.
     * Once the {@link ChatMemory} instance is returned, it's retained in memory and managed by {@link com.networknt.agent.service.AiServices}.
     *
     * @param memoryId The ID of the chat memory.
     * @return A {@link ChatMemory} instance.
     * @see MemoryId
     */
    ChatMemory get(Object memoryId);
}
