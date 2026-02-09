package com.networknt.agent.service;

import com.networknt.agent.exception.LangChain4jException;
import com.networknt.agent.model.moderation.Moderation;

/**
 * Thrown when content moderation fails, i.e.,
 * when content is flagged by the moderation model.
 *
 * @see Moderate
 * @see com.networknt.agent.model.moderation.ModerationModel
 */
public class ModerationException extends LangChain4jException {

    private final Moderation moderation;

    public ModerationException(String message, Moderation moderation) {
        super(message);
        this.moderation = moderation;
    }

    public Moderation moderation() {
        return moderation;
    }
}
