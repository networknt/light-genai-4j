package com.networknt.genai.guardrail;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.invocation.InvocationContext;
import com.networknt.genai.model.chat.ChatModel;
import com.networknt.genai.model.chat.StreamingChatModel;
import com.networknt.genai.model.chat.request.ChatRequest;
import com.networknt.genai.model.chat.response.ChatResponse;
import com.networknt.genai.observability.api.AiServiceListenerRegistrar;
import com.networknt.genai.observability.api.event.AiServiceEvent;
import com.networknt.genai.observability.api.listener.AiServiceListener;
import java.util.List;
import java.util.function.Consumer;

/**
 * Generic executor interface that defines a chat interaction
 */
public interface ChatExecutor {

    /**
     * Execute a chat request
     * @return The response
     */
    ChatResponse execute();

    /**
     * Executes a chat request using the provided chat messages
     * @param chatMessages The chat messages containing the context of the conversation.
     *                     It provides the history of messages required for proper interaction with the chat model
     * @return A response object containing the AI's response and additional metadata.
     */
    ChatResponse execute(List<ChatMessage> chatMessages);

    /**
     * Creates a new {@link SynchronousBuilder} instance for constructing {@link ChatExecutor} objects
     * that perform synchronous chat requests.
     *
     * @return A new {@link SynchronousBuilder} instance to configure and build a {@link ChatExecutor}.
     */
    static SynchronousBuilder builder(ChatModel chatModel) {
        return new SynchronousBuilder(chatModel);
    }

    /**
     * Creates a new {@link StreamingToSynchronousBuilder} instance for constructing {@link ChatExecutor} objects
     * that perform streaming chat requests.
     *
     * @return A new {@link StreamingToSynchronousBuilder} instance to configure and build a {@link ChatExecutor}.
     */
    static StreamingToSynchronousBuilder builder(StreamingChatModel streamingChatModel) {
        return new StreamingToSynchronousBuilder(streamingChatModel);
    }

    /**
     * An abstract base-builder class for constructing instances of {@link ChatExecutor}.
     *
     * This class provides a fluent API for setting required components, such as
     * {@link ChatRequest}, and defines a contract for building {@link ChatExecutor}
     * instances. Subclasses should implement the {@code build()} method to ensure
     * proper construction of the target chat executor object.
     *
     * @param <T> the type of the builder subclass for enabling fluent method chaining
     */
    abstract class AbstractBuilder<T extends AbstractBuilder<T>> {
        /**
         * The chat request.
         */
        protected ChatRequest chatRequest;
        /**
         * The invocation context.
         */
        protected InvocationContext invocationContext;
        /**
         * The event listener registrar.
         */
        protected AiServiceListenerRegistrar eventListenerRegistrar;

        /**
         * Default constructor.
         */
        protected AbstractBuilder() {}

        /**
         * Sets the {@link ChatRequest} instance for the synchronousBuilder.
         * The {@link ChatRequest} encapsulates the input messages and parameters required
         * to generate a response from the chat model.
         *
         * @param chatRequest the {@link ChatRequest} containing the input messages and parameters
         * @return the updated SynchronousBuilder instance
         */
        public AbstractBuilder<T> chatRequest(ChatRequest chatRequest) {
            this.chatRequest = chatRequest;
            return this;
        }

        /**
         * Sets the {@link InvocationContext} instance for the builder.
         * The {@link InvocationContext} provides contextual information
         * that can be used during the execution of the chat request.
         *
         * @param invocationContext the {@link InvocationContext} containing contextual information
         * @return the updated builder instance of type {@code T} for method chaining
         */
        public AbstractBuilder<T> invocationContext(InvocationContext invocationContext) {
            this.invocationContext = invocationContext;
            return this;
        }

        /**
         * Sets the {@link AiServiceListenerRegistrar} instance for the builder.
         * The {@link AiServiceListenerRegistrar} facilitates the registration and
         * management of {@link AiServiceListener}s, allowing the builder to
         * configure event listeners for handling {@link AiServiceEvent}s.
         *
         * @param eventListenerRegistrar the {@link AiServiceListenerRegistrar} to use for managing event listeners
         * @return the updated builder instance of type {@code T} for method chaining
         */
        public AbstractBuilder<T> eventListenerRegistrar(AiServiceListenerRegistrar eventListenerRegistrar) {
            this.eventListenerRegistrar = eventListenerRegistrar;
            return this;
        }

        /**
         * Constructs and returns an instance of {@link ChatExecutor}.
         * Ensures that all required parameters have been appropriately set
         * before building the {@link ChatExecutor}.
         *
         * @return a fully constructed {@link ChatExecutor} instance
         */
        public abstract ChatExecutor build();
    }

    /**
     * SynchronousBuilder for constructing instances of {@link ChatExecutor}.
     *
     * This synchronousBuilder provides a fluent API for setting required components
     * like {@link ChatRequest}, and for building an instance of the {@link ChatExecutor}.
     */
    class SynchronousBuilder extends AbstractBuilder<SynchronousBuilder> {
        /**
         * The chat model.
         */
        protected final ChatModel chatModel;

        /**
         * Creates a new synchronous builder.
         *
         * @param chatModel the chat model
         */
        protected SynchronousBuilder(ChatModel chatModel) {
            this.chatModel = ensureNotNull(chatModel, "chatModel");
        }

        /**
         * Constructs and returns an instance of {@link ChatExecutor}.
         * Ensures that all required parameters have been appropriately set
         * before building the {@link ChatExecutor}.
         *
         * @return a fully constructed {@link ChatExecutor} instance
         */
        public ChatExecutor build() {
            return new SynchronousChatExecutor(this);
        }
    }

    /**
     * StreamingToSynchronousBuilder for constructing instances of {@link ChatExecutor}.
     *
     * This streaming build provides a fluent API for setting required components
     * like {@link ChatRequest}, and for building an instance of the {@link ChatExecutor}
     * that simulates streaming.
     */
    class StreamingToSynchronousBuilder extends AbstractBuilder<StreamingToSynchronousBuilder> {
        /**
         * The streaming chat model.
         */
        protected final StreamingChatModel streamingChatModel;
        /**
         * The error handler.
         */
        protected Consumer<Throwable> errorHandler;

        /**
         * Creates a new streaming to synchronous builder.
         *
         * @param streamingChatModel the streaming chat model
         */
        protected StreamingToSynchronousBuilder(StreamingChatModel streamingChatModel) {
            this.streamingChatModel = ensureNotNull(streamingChatModel, "streamingChatModel");
        }

        /**
         * Sets a custom error handler to manage exceptions or errors that occur during the execution.
         *
         * @param errorHandler a {@link Consumer} of {@link Throwable} that processes the error
         * @return the current {@link StreamingToSynchronousBuilder} instance for method chaining
         */
        public StreamingToSynchronousBuilder errorHandler(Consumer<Throwable> errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        /**
         * Constructs and returns an instance of {@link ChatExecutor}.
         * Ensures that all required parameters have been appropriately set
         * before building the {@link ChatExecutor}.
         *
         * @return a fully constructed {@link ChatExecutor} instance
         */
        public ChatExecutor build() {
            return new StreamingToSynchronousChatExecutor(this);
        }
    }
}
