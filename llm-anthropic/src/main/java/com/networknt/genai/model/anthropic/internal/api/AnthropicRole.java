package com.networknt.genai.model.anthropic.internal.api;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum AnthropicRole {

    USER,
    ASSISTANT;

    @JsonValue
    public String serialize() {
        return name().toLowerCase(Locale.ROOT);
    }
}
