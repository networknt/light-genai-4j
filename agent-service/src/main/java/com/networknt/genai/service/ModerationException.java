package com.networknt.genai.service;

import com.networknt.genai.exception.LangChain4jException;
import com.networknt.genai.model.moderation.Moderation;

/**
 * Thrown when content moderation fails, i.e.,
 * when content is flagged by the moderation model.
 *
 * @see Moderate
 * @see com.networknt.genai.model.moderation.ModerationModel
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
