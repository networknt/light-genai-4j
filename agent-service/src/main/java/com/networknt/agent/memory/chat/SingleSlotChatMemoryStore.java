package com.networknt.agent.memory.chat;

import com.networknt.agent.Internal;
import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.memory.ChatMemory;
import com.networknt.agent.store.memory.chat.ChatMemoryStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link ChatMemoryStore} that stores state of {@link ChatMemory} (chat messages) in-memory.
 * <p>
 * This storage mechanism is transient and does not persist data across application restarts.
 */
@Internal
class SingleSlotChatMemoryStore implements ChatMemoryStore {

    private List<ChatMessage> messages = new ArrayList<>();

    private final Object memoryId;

    public SingleSlotChatMemoryStore(final Object memoryId) {
        this.memoryId = memoryId;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        checkMemoryId(memoryId);
        return messages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        checkMemoryId(memoryId);
        this.messages = messages;
    }

    @Override
    public void deleteMessages(Object memoryId) {
        checkMemoryId(memoryId);
        this.messages = new ArrayList<>();
    }

    private void checkMemoryId(Object memoryId) {
        if (!this.memoryId.equals(memoryId)) {
            throw new IllegalStateException("This chat memory has id: " + this.memoryId +
                    " but an operation has been requested on a memory with id: " + memoryId);
        }
    }
}
