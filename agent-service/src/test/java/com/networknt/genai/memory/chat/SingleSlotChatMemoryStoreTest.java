package com.networknt.genai.memory.chat;

import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.data.message.UserMessage;
import java.util.Arrays;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class SingleSlotChatMemoryStoreTest implements WithAssertions {
    @Test
    void test() {
        SingleSlotChatMemoryStore store = new SingleSlotChatMemoryStore("foo");
        assertThat(store.getMessages("foo")).isEmpty();

        store.updateMessages("foo", Arrays.asList(new UserMessage("abc def"), new AiMessage("ghi jkl")));

        assertThat(store.getMessages("foo")).containsExactly(new UserMessage("abc def"), new AiMessage("ghi jkl"));

        store.deleteMessages("foo");

        assertThat(store.getMessages("foo")).isEmpty();
    }
}
