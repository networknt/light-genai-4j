package com.networknt.model.ollama;

import static com.networknt.agent.client.sse.ServerSentEventListenerUtils.ignoringExceptions;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.networknt.agent.Internal;
import com.networknt.agent.client.sse.DefaultServerSentEventParsingHandle;
import com.networknt.agent.client.sse.ServerSentEvent;
import com.networknt.agent.client.sse.ServerSentEventContext;
import com.networknt.agent.client.sse.ServerSentEventListener;
import com.networknt.agent.client.sse.ServerSentEventParser;
import com.networknt.agent.client.sse.ServerSentEventParsingHandle;

/**
 * Ollama does not follow SSE standard for streaming, it uses newline delimited JSON format.
 * The HTTP response body from Ollama looks like this:
 * <pre>
 * {"model":"tinydolphin","created_at":"2025-01-22T11:22:42.180546Z","message":{"role":"assistant","content":" The"},"done":false}
 * {"model":"tinydolphin","created_at":"2025-01-22T11:22:42.186813Z","message":{"role":"assistant","content":" capital"},"done":false}
 * ...
 * {"model":"tinydolphin","created_at":"2025-01-22T11:22:42.223184Z","message":{"role":"assistant","content":""},"done_reason":"stop","done":true,"total_duration":181553584,"load_duration":27906292,"prompt_eval_count":36,"prompt_eval_duration":109000000,"eval_count":8,"eval_duration":42000000}
 * </pre>
 */
@Internal
class OllamaServerSentEventParser implements ServerSentEventParser {

    @Override
    public void parse(InputStream httpResponseBody, ServerSentEventListener listener) {
        ServerSentEventParsingHandle parsingHandle = new DefaultServerSentEventParsingHandle(httpResponseBody);
        ServerSentEventContext context = new ServerSentEventContext(parsingHandle);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponseBody, UTF_8))) {
            String line;
            while (!parsingHandle.isCancelled() && (line = reader.readLine()) != null) {
                ServerSentEvent sse = new ServerSentEvent(null, line);
                ignoringExceptions(() -> listener.onEvent(sse, context));
            }
        } catch (IOException e) {
            ignoringExceptions(() -> listener.onError(e));
        }
    }
}
