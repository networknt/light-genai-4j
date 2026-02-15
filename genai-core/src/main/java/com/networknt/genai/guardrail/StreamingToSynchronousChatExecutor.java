package com.networknt.genai.guardrail;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.Internal;
import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.model.chat.response.StreamingChatResponseHandler;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A concrete implementation of the {@link ChatExecutor} interface that executes
 * chat requests using a specified {@link StreamingChatModel}. It then executes the requests as if it were
 * synchronous, essentially transforming a streaming request to a synchronous request
 *
 * This class utilizes a {@link ChatRequest} to encapsulate the input messages
 *  and parameters and delegates the execution of the chat to the provided {@link StreamingChatModel}.
 *
 *  Instances of this class are immutable and are typically instantiated using
 *  the {@link StreamingToSynchronousBuilder}.
 */
@Internal
final class StreamingToSynchronousChatExecutor extends AbstractChatExecutor {
    private final StreamingChatModel streamingChatModel;
    private final Consumer<Throwable> errorHandler;

    protected StreamingToSynchronousChatExecutor(StreamingToSynchronousBuilder builder) {
        super(builder);

        this.streamingChatModel = ensureNotNull(builder.streamingChatModel, "streamingChatModel");
        this.errorHandler = builder.errorHandler;
    }

    @Override
    protected ChatResponse execute(ChatRequest chatRequest) {
        var responseHandler = new StreamingToSyncResponseHandler(this.errorHandler);
        this.streamingChatModel.chat(chatRequest, responseHandler);

        return Optional.ofNullable(responseHandler.getResponse()).orElseGet(ChatResponse.builder()::build);
    }

    private static class StreamingToSyncResponseHandler implements StreamingChatResponseHandler {
        private static final Logger LOG = LoggerFactory.getLogger(StreamingToSyncResponseHandler.class);
        private final Consumer<Throwable> errorHandler;
        private final CountDownLatch latch = new CountDownLatch(1);
        private AtomicReference<ChatResponse> response = new AtomicReference<>();

        StreamingToSyncResponseHandler(Consumer<Throwable> errorHandler) {
            this.errorHandler = errorHandler;
        }

        @Override
        public void onPartialResponse(String partialResponse) {}

        @Override
        public void onCompleteResponse(ChatResponse completeResponse) {
            response.set(completeResponse);
            this.latch.countDown();
        }

        private void waitForCompletion() {
            try {
                this.latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        ChatResponse getResponse() {
            waitForCompletion();
            return this.response.get();
        }

        @Override
        public void onError(Throwable error) {
            if (errorHandler != null) {
                try {
                    errorHandler.accept(error);
                } catch (Exception e) {
                    LOG.error("While handling the following error...", error);
                    LOG.error("...the following error happened", e);
                }
            } else {
                LOG.warn("Ignored error", error);
            }
        }
    }
}
