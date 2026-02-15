package com.networknt.genai.rag.content.aggregator;

import com.networknt.genai.data.segment.TextSegment;
import com.networknt.genai.model.scoring.ScoringModel;
import com.networknt.genai.rag.content.Content;
import com.networknt.genai.rag.content.ContentMetadata;
import com.networknt.genai.rag.query.Query;
import com.networknt.genai.rag.query.transformer.ExpandingQueryTransformer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.networknt.genai.internal.Exceptions.illegalArgument;
import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static com.networknt.genai.rag.content.ContentMetadata.RERANKED_SCORE;
import static java.util.Collections.emptyList;

/**
 * A {@link ContentAggregator} that performs re-ranking using a {@link ScoringModel}, such as Cohere.
 * <br>
 * The {@link ScoringModel} scores {@link Content}s against a (single) {@link Query}.
 * If multiple {@link Query}s are input to this aggregator
 * (for example, when using {@link ExpandingQueryTransformer}),
 * a {@link #querySelector} must be provided to select a {@link Query} for ranking all {@link Content}s.
 * Alternatively, a custom implementation can be created to score {@link Content}s against the {@link Query}s
 * that were used for their retrieval (instead of a single {@link Query}), and then re-rank based on those scores.
 * Although potentially more costly, this method may yield better results
 * when the {@link Query}s are significantly different.
 * <br>
 * <br>
 * Before the use of a {@link ScoringModel}, all {@link Content}s are fused in the same way
 * as by the {@link DefaultContentAggregator}. For detailed information, please refer to its Javadoc.
 * <br>
 * <br>
 * Configurable parameters (optional):
 * <br>
 * - {@link #minScore}: the minimum score for {@link Content}s to be returned.
 * {@link Content}s scoring below this threshold (as determined by the {@link ScoringModel})
 * are excluded from the results.
 *
 * @see DefaultContentAggregator
 */
public class ReRankingContentAggregator implements ContentAggregator {

    /**
     * The default query selector that selects a single query if only one is present.
     */
    public static final Function<Map<Query, Collection<List<Content>>>, Query> DEFAULT_QUERY_SELECTOR =
            (queryToContents) -> {
                if (queryToContents.size() > 1) {
                    throw illegalArgument(
                            "The 'queryToContents' contains %s queries, making the re-ranking ambiguous. " +
                                    "Because there are multiple queries, it is unclear which one should be " +
                                    "used for re-ranking. Please provide a 'querySelector' in the constructor/builder.",
                            queryToContents.size()
                    );
                }
                return queryToContents.keySet().iterator().next();
            };

    private final ScoringModel scoringModel;
    private final Function<Map<Query, Collection<List<Content>>>, Query> querySelector;
    private final Double minScore;
    private final Integer maxResults;

    /**
     * Creates a new re-ranking content aggregator with the given scoring model.
     *
     * @param scoringModel the scoring model
     */
    public ReRankingContentAggregator(ScoringModel scoringModel) {
        this(scoringModel, DEFAULT_QUERY_SELECTOR, null);
    }

    /**
     * Creates a new re-ranking content aggregator.
     *
     * @param scoringModel the scoring model
     * @param querySelector the query selector
     * @param minScore the minimum score
     */
    public ReRankingContentAggregator(ScoringModel scoringModel,
                                      Function<Map<Query, Collection<List<Content>>>, Query> querySelector,
                                      Double minScore) {
        this(scoringModel, querySelector, minScore, null);
    }

    /**
     * Creates a new re-ranking content aggregator.
     *
     * @param scoringModel the scoring model
     * @param querySelector the query selector
     * @param minScore the minimum score
     * @param maxResults the maximum number of results to return
     */
    public ReRankingContentAggregator(ScoringModel scoringModel,
                                      Function<Map<Query, Collection<List<Content>>>, Query> querySelector,
                                      Double minScore,
                                      Integer maxResults) {
        this.scoringModel = ensureNotNull(scoringModel, "scoringModel");
        this.querySelector = getOrDefault(querySelector, DEFAULT_QUERY_SELECTOR);
        this.minScore = minScore;
        this.maxResults = getOrDefault(maxResults, Integer.MAX_VALUE);
    }

    public static ReRankingContentAggregatorBuilder builder() {
        return new ReRankingContentAggregatorBuilder();
    }

    @Override
    public List<Content> aggregate(Map<Query, Collection<List<Content>>> queryToContents) {

        if (queryToContents.isEmpty()) {
            return emptyList();
        }

        // Select a query against which all contents will be re-ranked
        Query query = querySelector.apply(queryToContents);

        // For each query, fuse all contents retrieved from different sources using that query
        Map<Query, List<Content>> queryToFusedContents = fuse(queryToContents);

        // Fuse all contents retrieved using all queries
        List<Content> fusedContents = ReciprocalRankFuser.fuse(queryToFusedContents.values());

        if (fusedContents.isEmpty()) {
            return fusedContents;
        }

        // Re-rank all the fused contents against the query selected by the query selector
        return reRankAndFilter(fusedContents, query);
    }

    protected Map<Query, List<Content>> fuse(Map<Query, Collection<List<Content>>> queryToContents) {
        Map<Query, List<Content>> fused = new LinkedHashMap<>();
        for (Query query : queryToContents.keySet()) {
            Collection<List<Content>> contents = queryToContents.get(query);
            fused.put(query, ReciprocalRankFuser.fuse(contents));
        }
        return fused;
    }

    protected List<Content> reRankAndFilter(List<Content> contents, Query query) {

        List<TextSegment> segments = contents.stream()
                .map(Content::textSegment)
                .collect(Collectors.toList());

        List<Double> scores = scoringModel.scoreAll(segments, query.text()).content();

        Map<TextSegment, Double> segmentToScore = new HashMap<>();
        for (int i = 0; i < segments.size(); i++) {
            segmentToScore.put(segments.get(i), scores.get(i));
        }

        return segmentToScore.entrySet().stream()
                .filter(entry -> minScore == null || entry.getValue() >= minScore)
                .sorted(Map.Entry.<TextSegment, Double>comparingByValue().reversed())
                .map(entry ->  Content.from(entry.getKey(), Map.of(RERANKED_SCORE, entry.getValue())))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    /**
     * Builder for {@link ReRankingContentAggregator}.
     */
    public static class ReRankingContentAggregatorBuilder {
        private ScoringModel scoringModel;
        private Function<Map<Query, Collection<List<Content>>>, Query> querySelector;
        private Double minScore;
        private Integer maxResults;

        /**
         * Default constructor.
         */
        public ReRankingContentAggregatorBuilder() {
        }

        /**
         * Sets the scoring model.
         *
         * @param scoringModel the scoring model
         * @return the builder
         */
        public ReRankingContentAggregatorBuilder scoringModel(ScoringModel scoringModel) {
            this.scoringModel = scoringModel;
            return this;
        }

        /**
         * Sets the query selector.
         *
         * @param querySelector the query selector
         * @return the builder
         */
        public ReRankingContentAggregatorBuilder querySelector(Function<Map<Query, Collection<List<Content>>>, Query> querySelector) {
            this.querySelector = querySelector;
            return this;
        }

        /**
         * Sets the minimum score.
         *
         * @param minScore the minimum score
         * @return the builder
         */
        public ReRankingContentAggregatorBuilder minScore(Double minScore) {
            this.minScore = minScore;
            return this;
        }

        /**
         * Sets the maximum number of results to return.
         *
         * @param maxResults the maximum number of results
         * @return the builder
         */
        public ReRankingContentAggregatorBuilder maxResults(Integer maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        /**
         * Builds the re-ranking content aggregator.
         *
         * @return the re-ranking content aggregator
         */
        public ReRankingContentAggregator build() {
            return new ReRankingContentAggregator(this.scoringModel, this.querySelector, this.minScore, this.maxResults);
        }
    }
}
