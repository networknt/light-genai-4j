package com.networknt.agent.store.memory.chat;

import com.networknt.agent.data.message.AiMessage;
import com.networknt.agent.data.message.UserMessage;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class InMemoryChatMemoryStoreTest implements WithAssertions {
    @Test
    void test() {
        InMemoryChatMemoryStore store = new InMemoryChatMemoryStore();
        assertThat(store.getMessages("foo")).isEmpty();

        store.updateMessages("foo", Arrays.asList(new UserMessage("abc def"), new AiMessage("ghi jkl")));

        assertThat(store.getMessages("foo")).containsExactly(new UserMessage("abc def"), new AiMessage("ghi jkl"));

        store.deleteMessages("foo");

        assertThat(store.getMessages("foo")).isEmpty();
    }
}
