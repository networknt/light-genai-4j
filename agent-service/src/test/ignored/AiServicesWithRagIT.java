package com.networknt.agent.service;

import static com.networknt.agent.data.document.Metadata.metadata;
import static com.networknt.agent.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static com.networknt.agent.model.openai.OpenAiChatModelName.GPT_3_5_TURBO;
import static com.networknt.agent.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static com.networknt.agent.rag.query.router.LanguageModelQueryRouter.FallbackStrategy.FAIL;
import static com.networknt.agent.rag.query.router.LanguageModelQueryRouter.FallbackStrategy.ROUTE_TO_ALL;
import static com.networknt.agent.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.networknt.agent.data.document.Document;
import com.networknt.agent.data.document.DocumentSplitter;
import com.networknt.agent.data.document.parser.TextDocumentParser;
import com.networknt.agent.data.document.splitter.DocumentSplitters;
import com.networknt.agent.data.message.AiMessage;
import com.networknt.agent.data.message.ChatMessage;
import com.networknt.agent.data.message.UserMessage;
import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.memory.ChatMemory;
import com.networknt.agent.memory.chat.MessageWindowChatMemory;
import com.networknt.agent.model.TokenCountEstimator;
import com.networknt.agent.model.chat.ChatModel;
import com.networknt.agent.model.chat.mock.ChatModelMock;
import com.networknt.agent.model.embedding.EmbeddingModel;
import com.networknt.agent.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import com.networknt.agent.model.openai.OpenAiChatModel;
import com.networknt.agent.model.openai.OpenAiTokenCountEstimator;
import com.networknt.agent.model.output.Response;
import com.networknt.agent.model.scoring.ScoringModel;
import com.networknt.agent.rag.DefaultRetrievalAugmentor;
import com.networknt.agent.rag.content.Content;
import com.networknt.agent.rag.content.aggregator.ContentAggregator;
import com.networknt.agent.rag.content.aggregator.ReRankingContentAggregator;
import com.networknt.agent.rag.content.retriever.ContentRetriever;
import com.networknt.agent.rag.content.retriever.EmbeddingStoreContentRetriever;
import com.networknt.agent.rag.query.Query;
import com.networknt.agent.rag.query.router.LanguageModelQueryRouter;
import com.networknt.agent.rag.query.router.LanguageModelQueryRouter.FallbackStrategy;
import com.networknt.agent.rag.query.router.QueryRouter;
import com.networknt.agent.rag.query.transformer.ExpandingQueryTransformer;
import com.networknt.agent.rag.query.transformer.QueryTransformer;
import com.networknt.agent.store.embedding.EmbeddingStore;
import com.networknt.agent.store.embedding.EmbeddingStoreIngestor;
import com.networknt.agent.store.embedding.filter.Filter;
import com.networknt.agent.store.embedding.inmemory.InMemoryEmbeddingStore;
import com.networknt.agent.store.memory.chat.InMemoryChatMemoryStore;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class AiServicesWithRagIT {

    private static final String ALLOWED_CANCELLATION_PERIOD_DAYS = "61";
    private static final String MIN_BOOKING_PERIOD_DAYS = "17";

    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

    @BeforeEach
    void beforeEach() {
        ingest("miles-of-smiles-terms-of-use.txt", embeddingStore, embeddingModel);
    }

    interface Assistant {

        String answer(String query);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_content_retriever(ChatModel model) {

        // given
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .build();

        // when
        String answer = assistant.answer("Can I cancel my booking?");

        // then
        assertThat(answer).containsAnyOf(ALLOWED_CANCELLATION_PERIOD_DAYS, MIN_BOOKING_PERIOD_DAYS);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_content_retriever_and_chat_memory(ChatModel model) {

        // given
        ContentRetriever contentRetriever = spy(EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build());

        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        UserMessage userMessage = UserMessage.from("Hello");
        chatMemory.add(userMessage);
        AiMessage aiMessage = AiMessage.from("Hi, how can I help you today?");
        chatMemory.add(aiMessage);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .chatMemory(chatMemory)
                .build();

        String query = "In which cases can I cancel my booking?";

        // when
        String answer = assistant.answer(query);

        // then
        assertThat(answer).containsAnyOf(ALLOWED_CANCELLATION_PERIOD_DAYS, MIN_BOOKING_PERIOD_DAYS);

        verify(contentRetriever)
                .retrieve(argThat(q -> q.text().equals(query)
                        && q.metadata().chatMessage().equals(UserMessage.from(query))
                        && q.metadata().chatMemoryId().equals("default")
                        && q.metadata().chatMemory().equals(List.of(userMessage, aiMessage))));
        verifyNoMoreInteractions(contentRetriever);
    }

    interface MultiUserAssistant {

        String answer(@MemoryId int memoryId, @com.networknt.agent.service.UserMessage String query);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_content_retriever_and_chat_memory_provider(ChatModel model) {

        // given
        ContentRetriever contentRetriever = spy(EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build());

        MultiUserAssistant assistant = AiServices.builder(MultiUserAssistant.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();

        int memoryId = 1;

        String query = "Can I cancel my booking?";

        // when
        String answer = assistant.answer(memoryId, query);

        // then
        assertThat(answer).containsAnyOf(ALLOWED_CANCELLATION_PERIOD_DAYS, MIN_BOOKING_PERIOD_DAYS);

        verify(contentRetriever)
                .retrieve(argThat(q -> q.text().equals(query)
                        && q.metadata().chatMessage().equals(UserMessage.from(query))
                        && q.metadata().chatMemoryId().equals(memoryId)
                        && q.metadata().chatMemory().isEmpty()));
        verifyNoMoreInteractions(contentRetriever);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_query_transformer_and_content_retriever(ChatModel model) {

        // given
        QueryTransformer queryTransformer = new ExpandingQueryTransformer(model);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .retrievalAugmentor(DefaultRetrievalAugmentor.builder()
                        .queryTransformer(queryTransformer)
                        .contentRetriever(contentRetriever)
                        .build())
                .build();

        // when
        String answer = assistant.answer("Can I cancel my booking?");

        // then
        assertThat(answer).containsAnyOf(ALLOWED_CANCELLATION_PERIOD_DAYS, MIN_BOOKING_PERIOD_DAYS);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_query_router_and_content_retriever(ChatModel model) {

        // given
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build();

        ContentRetriever wrongContentRetriever = (query) -> {
            throw new RuntimeException("Should never be called");
        };

        Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();
        retrieverToDescription.put(contentRetriever, "car rental company terms of use");
        retrieverToDescription.put(wrongContentRetriever, "articles about cats");

        QueryRouter queryRouter = new LanguageModelQueryRouter(model, retrieverToDescription);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .retrievalAugmentor(DefaultRetrievalAugmentor.builder()
                        .queryRouter(queryRouter)
                        .build())
                .build();

        // when
        String answer = assistant.answer("Can I cancel my booking?");

        // then
        assertThat(answer).containsAnyOf(ALLOWED_CANCELLATION_PERIOD_DAYS, MIN_BOOKING_PERIOD_DAYS);
    }

    @Disabled("TODO fix")
    @ParameterizedTest
    @MethodSource("models")
    void should_not_route_when_query_is_ambiguous(ChatModel model) {

        // given
        String query = "Hey what's up?";

        ContentRetriever contentRetriever = mock(ContentRetriever.class);
        Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();
        retrieverToDescription.put(contentRetriever, "articles about cats");

        QueryRouter queryRouter = new LanguageModelQueryRouter(model, retrieverToDescription);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .retrievalAugmentor(DefaultRetrievalAugmentor.builder()
                        .queryRouter(queryRouter)
                        .build())
                .build();

        // when
        String answer = assistant.answer(query);

        // then
        assertThat(answer).isNotBlank();

        verifyNoInteractions(contentRetriever);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_route_to_all_retrievers_when_query_is_ambiguous(ChatModel model) {

        // given
        String query = "Hey what's up?";
        FallbackStrategy fallbackStrategy = ROUTE_TO_ALL;

        ContentRetriever contentRetriever1 = spy(EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build());

        ContentRetriever contentRetriever2 = spy(EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build());

        Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();
        retrieverToDescription.put(contentRetriever1, "car rental company terms of use 1");
        retrieverToDescription.put(contentRetriever2, "car rental company terms of use 2");

        QueryRouter queryRouter = LanguageModelQueryRouter.builder()
                .chatModel(model)
                .retrieverToDescription(retrieverToDescription)
                .fallbackStrategy(fallbackStrategy)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .retrievalAugmentor(DefaultRetrievalAugmentor.builder()
                        .queryRouter(queryRouter)
                        .build())
                .build();

        // when
        String answer = assistant.answer(query);

        // then
        assertThat(answer).isNotBlank();

        verify(contentRetriever1).retrieve(argThat(q -> q.text().equals(query)));
        verifyNoMoreInteractions(contentRetriever1);

        verify(contentRetriever2).retrieve(argThat(q -> q.text().equals(query)));
        verifyNoMoreInteractions(contentRetriever2);
    }

    @Disabled("Fixed in https://github.com/langchain4j/langchain4j/pull/2311")
    @ParameterizedTest
    @MethodSource("models")
    void should_fail_when_query_is_ambiguous(ChatModel model) {

        // given
        String query = "Hey what's up?";
        FallbackStrategy fallbackStrategy = FAIL;

        ContentRetriever contentRetriever = spy(EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build());

        Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();
        retrieverToDescription.put(contentRetriever, "car rental company terms of use");

        QueryRouter queryRouter = LanguageModelQueryRouter.builder()
                .chatModel(model)
                .retrieverToDescription(retrieverToDescription)
                .fallbackStrategy(fallbackStrategy)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .retrievalAugmentor(DefaultRetrievalAugmentor.builder()
                        .queryRouter(queryRouter)
                        .build())
                .build();

        // when-then
        assertThatThrownBy(() -> assistant.answer(query)).hasRootCauseExactlyInstanceOf(NumberFormatException.class);

        verifyNoInteractions(contentRetriever);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_content_retriever_and_content_aggregator(ChatModel model) {

        // given
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .build();

        ScoringModel scoringModel = mock(ScoringModel.class);
        when(scoringModel.scoreAll(any(), any())).thenReturn(Response.from(asList(0.9, 0.7)));
        ContentAggregator contentAggregator = new ReRankingContentAggregator(scoringModel);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .retrievalAugmentor(DefaultRetrievalAugmentor.builder()
                        .contentRetriever(contentRetriever)
                        .contentAggregator(contentAggregator)
                        .build())
                .build();

        // when
        String answer = assistant.answer("Can I cancel my booking?");

        // then
        assertThat(answer).containsAnyOf(ALLOWED_CANCELLATION_PERIOD_DAYS, MIN_BOOKING_PERIOD_DAYS);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_all_rag_components(ChatModel model) {

        // given
        QueryTransformer queryTransformer = new ExpandingQueryTransformer(model);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .build();
        ContentRetriever wrongContentRetriever = (query) -> {
            throw new RuntimeException("Should never be called");
        };
        Map<ContentRetriever, String> retrieverToDescription = new HashMap<>();
        retrieverToDescription.put(contentRetriever, "car rental company terms of use");
        retrieverToDescription.put(wrongContentRetriever, "articles about unicorns");
        QueryRouter queryRouter = new LanguageModelQueryRouter(model, retrieverToDescription);

        ScoringModel scoringModel = mock(ScoringModel.class);
        when(scoringModel.scoreAll(any(), any())).thenAnswer(invocation -> {
            List<TextSegment> segments = (List<TextSegment>) invocation.getArguments()[0];
            List<Double> scores = segments.stream()
                    .map(segment -> {
                        if (segment.text().contains(ALLOWED_CANCELLATION_PERIOD_DAYS)
                                || segment.text().contains(MIN_BOOKING_PERIOD_DAYS)) {
                            return 0.9;
                        } else {
                            return 0.1;
                        }
                    })
                    .toList();
            return Response.from(scores);
        });
        ContentAggregator contentAggregator = ReRankingContentAggregator.builder()
                .scoringModel(scoringModel)
                .querySelector(
                        (queryToContents) -> queryToContents.keySet().iterator().next())
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .retrievalAugmentor(DefaultRetrievalAugmentor.builder()
                        .queryTransformer(queryTransformer)
                        .queryRouter(queryRouter)
                        .contentAggregator(contentAggregator)
                        .build())
                .build();

        // when
        String answer = assistant.answer("Can I cancel my booking?");

        // then
        assertThat(answer).containsAnyOf(ALLOWED_CANCELLATION_PERIOD_DAYS, MIN_BOOKING_PERIOD_DAYS);
    }

    interface PersonalizedAssistant {

        String chat(@MemoryId String userId, @com.networknt.agent.service.UserMessage String userMessage);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_dynamicFilter_by_user_id(ChatModel model) {

        // given
        TextSegment user1Info = TextSegment.from("My favorite color is green", metadata("userId", "1"));
        TextSegment user2Info = TextSegment.from("My favorite color is red", metadata("userId", "2"));

        Function<Query, Filter> dynamicMetadataFilter = (query) ->
                metadataKey("userId").isEqualTo(query.metadata().chatMemoryId().toString());

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.add(embeddingModel.embed(user1Info).content(), user1Info);
        embeddingStore.add(embeddingModel.embed(user2Info).content(), user2Info);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .dynamicFilter(dynamicMetadataFilter)
                .build();

        PersonalizedAssistant personalizedAssistant = AiServices.builder(PersonalizedAssistant.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();

        // when
        String answer1 = personalizedAssistant.chat("1", "Which color would be best for a dress?");

        // then
        assertThat(answer1).containsIgnoringCase("green");

        // when
        String answer2 = personalizedAssistant.chat("2", "Which color would be best for a dress?");

        // then
        assertThat(answer2).containsIgnoringCase("red");
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_static_metadata_filter(ChatModel model) {

        // given
        TextSegment catsArticle = TextSegment.from("cats", metadata("animal", "cat"));
        TextSegment dogsArticle = TextSegment.from("dogs", metadata("animal", "dog"));

        Filter metadatafilter = metadataKey("animal").isEqualTo("dog");

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.add(embeddingModel.embed(catsArticle).content(), catsArticle);
        embeddingStore.add(embeddingModel.embed(dogsArticle).content(), dogsArticle);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .filter(metadatafilter)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .build();

        // when
        String answer = assistant.answer("Which animal is mentioned?");

        // then
        assertThat(answer).containsIgnoringCase("dog");
    }

    interface AssistantReturningResult {

        Result<String> answer(String query);
    }

    @ParameterizedTest
    @MethodSource("models")
    void should_use_content_retriever_and_return_sources_inside_result(ChatModel model) {

        // given
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build();

        AssistantReturningResult assistant = AiServices.builder(AssistantReturningResult.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .build();

        // when
        Result<String> result = assistant.answer("Can I cancel my booking?");

        // then
        assertThat(result.content()).containsAnyOf(ALLOWED_CANCELLATION_PERIOD_DAYS, MIN_BOOKING_PERIOD_DAYS);

        assertThat(result.tokenUsage()).isNotNull();

        assertThat(result.sources()).hasSize(1);
        Content content = result.sources().get(0);
        assertThat(content.textSegment().text())
                .isEqualToIgnoringWhitespace("4. Cancellation Policy"
                        + "4.1 Reservations can be cancelled up to 61 days prior to the start of the booking period."
                        + "4.2 If the booking period is less than 17 days, cancellations are not permitted.");
        assertThat(content.textSegment().metadata().getString("index")).isEqualTo("3");
        assertThat(content.textSegment().metadata().getString("file_name"))
                .isEqualTo("miles-of-smiles-terms-of-use.txt");
    }

    @Test
    void should_pass_custom_attributes_from_query_transformer_to_chat_memory_store() {

        // given
        String attributeKey = "attribute-key";
        String attributeValue = "attribute-value";

        QueryTransformer queryTransformer = new QueryTransformer() {
            @Override
            public Collection<Query> transform(Query query) {
                UserMessage userMessage = (UserMessage) query.metadata().chatMessage();
                userMessage.attributes().put(attributeKey, attributeValue);
                return List.of(query);
            }
        };

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .build();

        AtomicReference<String> observedAttributeValue = new AtomicReference<>();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryStore(new InMemoryChatMemoryStore() {
                    @Override
                    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
                        UserMessage userMessage = (UserMessage) messages.get(0);
                        observedAttributeValue.set(userMessage.attribute(attributeKey, String.class));
                        super.updateMessages(memoryId, messages);
                    }
                })
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(ChatModelMock.thatAlwaysResponds("does not matter"))
                .chatMemory(chatMemory)
                .retrievalAugmentor(DefaultRetrievalAugmentor.builder()
                        .queryTransformer(queryTransformer)
                        .contentRetriever(contentRetriever)
                        .build())
                .build();

        // when
        assistant.answer("does not matter");

        // then
        assertThat(observedAttributeValue).hasValue(attributeValue);
    }

    interface AssistantWithSystemMessage {
        @SystemMessage("You are a helpful assistant for a car rental company.")
        String answer(String query);
    }

    private void ingest(
            String documentPath, EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        TokenCountEstimator tokenCountEstimator = new OpenAiTokenCountEstimator(GPT_3_5_TURBO);
        DocumentSplitter splitter = DocumentSplitters.recursive(100, 0, tokenCountEstimator);
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        Document document = loadDocument(toPath(documentPath), new TextDocumentParser());
        ingestor.ingest(document);
    }

    static Stream<Arguments> models() {
        return Stream.of(
                Arguments.of(OpenAiChatModel.builder()
                        .baseUrl(System.getenv("OPENAI_BASE_URL"))
                        .apiKey(System.getenv("OPENAI_API_KEY"))
                        .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
                        .modelName(GPT_4_O_MINI)
                        .logRequests(true)
                        .logResponses(true)
                        .build())
                // TODO add more models
                );
    }

    private Path toPath(String fileName) {
        try {
            return Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
