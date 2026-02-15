package com.networknt.genai.model.anthropic.internal.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AnthropicToolChoiceType {
    @JsonProperty("auto")
    AUTO,
    @JsonProperty("any")
    ANY,
    @JsonProperty("tool")
    TOOL,
    @JsonProperty("none")
    NONE
}
