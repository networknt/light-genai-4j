package com.networknt.genai.model.chat.mock;

import static com.networknt.genai.internal.InternalStreamingChatResponseHandlerUtils.onCompleteResponse;
import static com.networknt.genai.internal.InternalStreamingChatResponseHandlerUtils.onCompleteToolCall;
import static com.networknt.genai.internal.InternalStreamingChatResponseHandlerUtils.onPartialResponse;
import static com.networknt.genai.internal.Utils.isNullOrEmpty;
import static com.networknt.genai.internal.ValidationUtils.ensureNotEmpty;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static java.util.Arrays.asList;

import com.networknt.genai.Experimental;
import com.networknt.genai.tool.ToolExecutionRequest;
import com.networknt.genai.data.message.AiMessage;
import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.chat.response.CompleteToolCall;
import com.networknt.genai.model.chat.response.StreamingChatResponseHandler;
import com.networknt.genai.model.chat.response.StreamingHandle;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

/**
 * An implementation of a {@link StreamingChatModel} useful for unit testing.
 * This implementation is experimental and subject to change in the future. It may utilize Mockito internally.
 */
@Experimental
public class StreamingChatModelMock implements StreamingChatModel {

    private final Queue<AiMessage> aiMessages;
    private final RuntimeException exception;

    public StreamingChatModelMock(List<String> tokens) {
        this(List.of(toAiMessage(tokens)));
    }

    public StreamingChatModelMock(Collection<AiMessage> aiMessages) {
        this.aiMessages = new ConcurrentLinkedQueue<>(ensureNotEmpty(aiMessages, "aiMessages"));
        this.exception = null;
    }

    public StreamingChatModelMock(RuntimeException exception) {
        this.aiMessages = null;
        this.exception = ensureNotNull(exception, "exception");
    }

    @Override
    public void doChat(ChatRequest chatRequest, StreamingChatResponseHandler handler) {
        if (exception != null) {
            handler.onError(exception);
        } else {
            AiMessage aiMessage = ensureNotNull(aiMessages.poll(), "aiMessage");

            var executor = Executors.newSingleThreadExecutor();

            try {
                executor.execute(() -> {

                    StreamingHandle streamingHandle = new SimpleStreamingHandle();

                    for (String token : toTokens(aiMessage)) {
                        if (streamingHandle.isCancelled()) {
                            return;
                        }

                        onPartialResponse(handler, token, streamingHandle);
                    }

                    for (int i = 0; i < aiMessage.toolExecutionRequests().size(); i++) {
                        ToolExecutionRequest toolExecutionRequest =
                                aiMessage.toolExecutionRequests().get(i);
                        CompleteToolCall completeToolCall = new CompleteToolCall(i, toolExecutionRequest);
                        onCompleteToolCall(handler, completeToolCall);
                    }

                    ChatResponse chatResponse =
                            ChatResponse.builder().aiMessage(aiMessage).build();

                    onCompleteResponse(handler, chatResponse);
                });
            } finally {
                executor.shutdown();
            }
        }
    }

    private static AiMessage toAiMessage(List<String> tokens) {
        String text = String.join("", tokens);
        return AiMessage.from(text);
    }

    static List<String> toTokens(AiMessage aiMessage) {
        if (isNullOrEmpty(aiMessage.text())) {
            return List.of();
        }

        // approximating: each char will become a token
        return aiMessage.text().chars().mapToObj(c -> String.valueOf((char) c)).toList();
    }

    private static class SimpleStreamingHandle implements StreamingHandle {

        private boolean isCancelled;

        @Override
        public void cancel() {
            isCancelled = true;
        }

        @Override
        public boolean isCancelled() {
            return isCancelled;
        }
    }

    public static StreamingChatModelMock thatAlwaysStreams(String... tokens) {
        return new StreamingChatModelMock(asList(tokens));
    }

    public static StreamingChatModelMock thatAlwaysStreams(List<String> tokens) {
        return new StreamingChatModelMock(tokens);
    }

    public static StreamingChatModelMock thatAlwaysStreams(AiMessage aiMessage) {
        return new StreamingChatModelMock(List.of(aiMessage));
    }

    public static StreamingChatModelMock thatAlwaysStreams(AiMessage... aiMessages) {
        return new StreamingChatModelMock(asList(aiMessages));
    }

    public static StreamingChatModelMock thatAlwaysStreams(Collection<AiMessage> aiMessages) {
        return new StreamingChatModelMock(aiMessages);
    }

    public static StreamingChatModelMock thatAlwaysThrowsException() {
        return thatAlwaysThrowsExceptionWithMessage("Something went wrong, but this is an expected exception");
    }

    public static StreamingChatModelMock thatAlwaysThrowsExceptionWithMessage(String message) {
        return new StreamingChatModelMock(new RuntimeException(message));
    }
}
