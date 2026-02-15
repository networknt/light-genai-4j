package com.networknt.genai.model.anthropic.internal.client;

import com.networknt.genai.client.SuccessfulHttpResponse;
import com.networknt.genai.model.anthropic.internal.api.AnthropicCreateMessageResponse;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

/**
 * @since 1.10.0
 */
public class ParsedAndRawResponse {

    private final AnthropicCreateMessageResponse parsedResponse;
    private final SuccessfulHttpResponse rawResponse;

    public ParsedAndRawResponse(AnthropicCreateMessageResponse parsedResponse, SuccessfulHttpResponse rawResponse) {
        this.parsedResponse = ensureNotNull(parsedResponse, "parsedResponse");
        this.rawResponse = rawResponse;
    }

    public AnthropicCreateMessageResponse parsedResponse() {
        return parsedResponse;
    }

    public SuccessfulHttpResponse rawResponse() {
        return rawResponse;
    }
}
