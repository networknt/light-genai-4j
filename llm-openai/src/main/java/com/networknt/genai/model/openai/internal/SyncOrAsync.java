package com.networknt.genai.model.openai.internal;

import com.networknt.genai.http.client.SuccessfulHttpResponse;
import java.util.function.Consumer;

public interface SyncOrAsync<ResponseContent> {

    ResponseContent execute();

    default ParsedAndRawResponse<ResponseContent> executeRaw() {
        ResponseContent parsedResponse = execute();
        SuccessfulHttpResponse rawHttpResponse = null;
        return new ParsedAndRawResponse<>(parsedResponse, rawHttpResponse);
    }

    AsyncResponseHandling onResponse(Consumer<ResponseContent> responseHandler);
}
