package com.networknt.agent.rag.content.injector;

import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.rag.content.Content;

import java.util.List;

/**
 * Injects given {@link Content}s into a given {@link UserMessage}.
 * <br>
 * The goal is to format and incorporate the {@link Content}s into the original {@link UserMessage}
 * enabling the LLM to utilize it for generating a grounded response.
 *
 * @see DefaultContentInjector
 */
public interface ContentInjector {

    /**
     * Injects given {@link Content}s into a given {@link ChatMessage}.
     *
     * @param contents    The list of {@link Content} to be injected.
     * @param chatMessage The {@link ChatMessage} into which the {@link Content}s are to be injected.
     *                    Currently, only {@link UserMessage} is supported.
     * @return The {@link ChatMessage} with the injected {@link Content}s.
     */
    ChatMessage inject(List<Content> contents, ChatMessage chatMessage);
}
