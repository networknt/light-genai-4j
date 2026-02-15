package com.networknt.genai.rag;

import com.networknt.genai.data.message.ChatMessage;
import com.networknt.genai.data.message.UserMessage;
import com.networknt.genai.rag.content.Content;
import com.networknt.genai.rag.content.aggregator.ContentAggregator;
import com.networknt.genai.rag.content.aggregator.DefaultContentAggregator;
import com.networknt.genai.rag.content.injector.ContentInjector;
import com.networknt.genai.rag.content.injector.DefaultContentInjector;
import com.networknt.genai.rag.content.retriever.ContentRetriever;
import com.networknt.genai.rag.query.Query;
import com.networknt.genai.rag.query.router.DefaultQueryRouter;
import com.networknt.genai.rag.query.router.QueryRouter;
import com.networknt.genai.rag.query.transformer.DefaultQueryTransformer;
import com.networknt.genai.rag.query.transformer.QueryTransformer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/**
 * The default implementation of {@link RetrievalAugmentor} intended to be suitable for the majority of use cases.
 * <br>
 * <br>
 * It's important to note that while efforts will be made to avoid breaking changes,
 * the default behavior of this class may be updated in the future if it's found
 * that the current behavior does not adequately serve the majority of use cases.
 * Such changes would be made to benefit both current and future users.
 * <br>
 * <br>
 * This implementation is inspired by <a href="https://blog.langchain.dev/deconstructing-rag">this article</a>
 * and <a href="https://arxiv.org/abs/2312.10997">this paper</a>.
 * It is recommended to review these resources for a better understanding of the concept.
 * <br>
 * <br>
 * This implementation orchestrates the flow between the following base components:
 * <pre>
 * - {@link QueryTransformer}
 * - {@link QueryRouter}
 * - {@link ContentRetriever}
 * - {@link ContentAggregator}
 * - {@link ContentInjector}
 * </pre>
 * Visual representation of this flow can be found
 * <a href="https://docs.langchain4j.dev/img/advanced-rag.png">here</a>.
 * <br>
 * For each base component listed above, we offer several ready-to-use implementations,
 * each based on a recognized approach.
 * We intend to introduce more such implementations over time and welcome your contributions.
 * <br>
 * <br>
 * The flow is as follows:
 * <br>
 * 1. A {@link Query} (derived from an original {@link UserMessage}) is transformed
 * using a {@link QueryTransformer} into one or multiple {@link Query}s.
 * <br>
 * 2. Each {@link Query} is routed to the appropriate {@link ContentRetriever} using a {@link QueryRouter}.
 * Each {@link ContentRetriever} retrieves one or multiple {@link Content}s using a {@link Query}.
 * <br>
 * 3. All {@link Content}s retrieved by all {@link ContentRetriever}s using all {@link Query}s are
 * aggregated (fused/re-ranked/filtered/etc.) into a final list of {@link Content}s using a {@link ContentAggregator}.
 * <br>
 * 4. Lastly, a final list of {@link Content}s is injected into the original {@link UserMessage}
 * using a {@link ContentInjector}.
 * <br>
 * <br>
 * By default, each base component (except for {@link ContentRetriever}, which needs to be provided by you)
 * is initialized with a sensible default implementation:
 * <pre>
 * - {@link DefaultQueryTransformer}
 * - {@link DefaultQueryRouter}
 * - {@link DefaultContentAggregator}
 * - {@link DefaultContentInjector}
 * </pre>
 * Nonetheless, you are encouraged to use one of the advanced ready-to-use implementations or create a custom one.
 * <br>
 * <br>
 * When there is only a single {@link Query} and a single {@link ContentRetriever},
 * query routing and content retrieval are performed in the same thread.
 * Otherwise, an {@link Executor} is used to parallelize the processing.
 * By default, a modified (keepAliveTime is 1 second instead of 60 seconds) {@link Executors#newCachedThreadPool()}
 * is used, but you can provide a custom {@link Executor} instance.
 *
 * @see DefaultQueryTransformer
 * @see DefaultQueryRouter
 * @see DefaultContentAggregator
 * @see DefaultContentInjector
 */
public class DefaultRetrievalAugmentor implements RetrievalAugmentor {

    private final QueryTransformer queryTransformer;
    private final QueryRouter queryRouter;
    private final ContentAggregator contentAggregator;
    private final ContentInjector contentInjector;
    private final Executor executor;

    /**
     * Creates a new default retrieval augmentor.
     *
     * @param queryTransformer  the query transformer
     * @param queryRouter       the query router
     * @param contentAggregator the content aggregator
     * @param contentInjector   the content injector
     * @param executor          the executor
     */
    public DefaultRetrievalAugmentor(QueryTransformer queryTransformer,
                                     QueryRouter queryRouter,
                                     ContentAggregator contentAggregator,
                                     ContentInjector contentInjector,
                                     Executor executor) {
        this.queryTransformer = getOrDefault(queryTransformer, DefaultQueryTransformer::new);
        this.queryRouter = ensureNotNull(queryRouter, "queryRouter");
        this.contentAggregator = getOrDefault(contentAggregator, DefaultContentAggregator::new);
        this.contentInjector = getOrDefault(contentInjector, DefaultContentInjector::new);
        this.executor = getOrDefault(executor, DefaultRetrievalAugmentor::createDefaultExecutor);
    }

    private static ExecutorService createDefaultExecutor() {
        return new ThreadPoolExecutor(
            0, Integer.MAX_VALUE,
            1, SECONDS,
            new SynchronousQueue<>()
        );
    }

    @Override
    public AugmentationResult augment(AugmentationRequest augmentationRequest) {

        ChatMessage chatMessage = augmentationRequest.chatMessage();
        String queryText;
        if (chatMessage instanceof UserMessage userMessage) {
            queryText = userMessage.singleText();
        } else {
            throw new IllegalArgumentException("Unsupported message type: " + chatMessage.type());
        }
        Query originalQuery = Query.from(queryText, augmentationRequest.metadata());

        Collection<Query> queries = queryTransformer.transform(originalQuery);

        Map<Query, Collection<List<Content>>> queryToContents = process(queries);

        List<Content> contents = contentAggregator.aggregate(queryToContents);

        ChatMessage augmentedChatMessage = contentInjector.inject(contents, chatMessage);

        return AugmentationResult.builder()
            .chatMessage(augmentedChatMessage)
            .contents(contents)
            .build();
    }

    private Map<Query, Collection<List<Content>>> process(Collection<Query> queries) {
        if (queries.size() == 1) {
            Query query = queries.iterator().next();
            Collection<ContentRetriever> retrievers = queryRouter.route(query);
            if (retrievers.size() == 1) {
                ContentRetriever contentRetriever = retrievers.iterator().next();
                List<Content> contents = contentRetriever.retrieve(query);
                return singletonMap(query, singletonList(contents));
            } else if (retrievers.size() > 1) {
                Collection<List<Content>> contents = retrieveFromAll(retrievers, query).join();
                return singletonMap(query, contents);
            } else {
                return emptyMap();
            }
        } else if (queries.size() > 1) {
            Map<Query, CompletableFuture<Collection<List<Content>>>> queryToFutureContents = new ConcurrentHashMap<>();
            queries.forEach(query -> {
                CompletableFuture<Collection<List<Content>>> futureContents =
                        supplyAsync(() -> queryRouter.route(query), executor)
                                .thenCompose(retrievers -> retrieveFromAll(retrievers, query));
                queryToFutureContents.put(query, futureContents);
            });
            return join(queryToFutureContents);
        } else {
            return emptyMap();
        }
    }

    private CompletableFuture<Collection<List<Content>>> retrieveFromAll(Collection<ContentRetriever> retrievers,
                                                                         Query query) {
        List<CompletableFuture<List<Content>>> futureContents = retrievers.stream()
            .map(retriever -> supplyAsync(() -> retriever.retrieve(query), executor))
            .collect(Collectors.toList());

        return allOf(futureContents.toArray(new CompletableFuture[0]))
            .thenApply(ignored ->
                futureContents.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
    }

    private static Map<Query, Collection<List<Content>>> join(
        Map<Query, CompletableFuture<Collection<List<Content>>>> queryToFutureContents) {
        return allOf(queryToFutureContents.values().toArray(new CompletableFuture[0]))
            .thenApply(ignored ->
                queryToFutureContents.entrySet().stream()
                    .collect(toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().join()
                    ))
            ).join();
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    public static DefaultRetrievalAugmentorBuilder builder() {
        return new DefaultRetrievalAugmentorBuilder();
    }

    /**
     * Builder for {@link DefaultRetrievalAugmentor}.
     */
    public static class DefaultRetrievalAugmentorBuilder {

        private QueryTransformer queryTransformer;
        private QueryRouter queryRouter;
        private ContentAggregator contentAggregator;
        private ContentInjector contentInjector;
        private Executor executor;

        DefaultRetrievalAugmentorBuilder() {
        }

        /**
         * Sets the content retriever.
         *
         * @param contentRetriever the content retriever
         * @return the builder
         */
        public DefaultRetrievalAugmentorBuilder contentRetriever(ContentRetriever contentRetriever) {
            this.queryRouter = new DefaultQueryRouter(ensureNotNull(contentRetriever, "contentRetriever"));
            return this;
        }

        /**
         * Sets the query transformer.
         *
         * @param queryTransformer the query transformer
         * @return the builder
         */
        public DefaultRetrievalAugmentorBuilder queryTransformer(QueryTransformer queryTransformer) {
            this.queryTransformer = queryTransformer;
            return this;
        }

        /**
         * Sets the query router.
         *
         * @param queryRouter the query router
         * @return the builder
         */
        public DefaultRetrievalAugmentorBuilder queryRouter(QueryRouter queryRouter) {
            this.queryRouter = queryRouter;
            return this;
        }

        /**
         * Sets the content aggregator.
         *
         * @param contentAggregator the content aggregator
         * @return the builder
         */
        public DefaultRetrievalAugmentorBuilder contentAggregator(ContentAggregator contentAggregator) {
            this.contentAggregator = contentAggregator;
            return this;
        }

        /**
         * Sets the content injector.
         *
         * @param contentInjector the content injector
         * @return the builder
         */
        public DefaultRetrievalAugmentorBuilder contentInjector(ContentInjector contentInjector) {
            this.contentInjector = contentInjector;
            return this;
        }

        /**
         * Sets the executor.
         *
         * @param executor the executor
         * @return the builder
         */
        public DefaultRetrievalAugmentorBuilder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Builds the default retrieval augmentor.
         *
         * @return the default retrieval augmentor
         */
        public DefaultRetrievalAugmentor build() {
            return new DefaultRetrievalAugmentor(this.queryTransformer, this.queryRouter, this.contentAggregator, this.contentInjector, this.executor);
        }
    }
}
