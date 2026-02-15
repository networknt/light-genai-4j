package com.networknt.genai.store.memory.chat;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.UserMessage;
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
