package com.networknt.genai.rag.content.retriever;

import com.networknt.genai.rag.content.Content;
import com.networknt.genai.rag.query.Query;
import com.networknt.genai.web.search.WebSearchEngine;
import com.networknt.genai.web.search.WebSearchRequest;
import com.networknt.genai.web.search.WebSearchResults;

import java.util.List;

import static com.networknt.genai.internal.Utils.getOrDefault;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;
import static java.util.stream.Collectors.toList;

/**
 * A {@link ContentRetriever} that retrieves relevant {@link Content} from the web using a {@link WebSearchEngine}.
 * <br>
 * It returns one {@link Content} for each result that a {@link WebSearchEngine} has returned for a given {@link Query}.
 * <br>
 * Depending on the {@link WebSearchEngine} implementation, the {@link Content#textSegment()}
 * can contain either a snippet of a web page or a complete content of a web page.
 */
public class WebSearchContentRetriever implements ContentRetriever {

    private final WebSearchEngine webSearchEngine;
    private final int maxResults;

    /**
     * Creates a new web search content retriever.
     *
     * @param webSearchEngine the web search engine
     * @param maxResults      the maximum number of results
     */
    public WebSearchContentRetriever(WebSearchEngine webSearchEngine, Integer maxResults) {
        this.webSearchEngine = ensureNotNull(webSearchEngine, "webSearchEngine");
        this.maxResults = getOrDefault(maxResults, 5);
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    public static WebSearchContentRetrieverBuilder builder() {
        return new WebSearchContentRetrieverBuilder();
    }

    @Override
    public List<Content> retrieve(Query query) {

        WebSearchRequest webSearchRequest = WebSearchRequest.builder()
                .searchTerms(query.text())
                .maxResults(maxResults)
                .build();

        WebSearchResults webSearchResults = webSearchEngine.search(webSearchRequest);

        return webSearchResults.toTextSegments().stream()
                .map(Content::from)
                .collect(toList());
    }

    /**
     * Builder for {@link WebSearchContentRetriever}.
     */
    public static class WebSearchContentRetrieverBuilder {
        private WebSearchEngine webSearchEngine;
        private Integer maxResults;

        WebSearchContentRetrieverBuilder() {
        }

        /**
         * Sets the web search engine.
         *
         * @param webSearchEngine the web search engine
         * @return the builder
         */
        public WebSearchContentRetrieverBuilder webSearchEngine(WebSearchEngine webSearchEngine) {
            this.webSearchEngine = webSearchEngine;
            return this;
        }

        /**
         * Sets the maximum number of results.
         *
         * @param maxResults the maximum number of results
         * @return the builder
         */
        public WebSearchContentRetrieverBuilder maxResults(Integer maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        /**
         * Builds the web search content retriever.
         *
         * @return the web search content retriever
         */
        public WebSearchContentRetriever build() {
            return new WebSearchContentRetriever(this.webSearchEngine, this.maxResults);
        }
    }
}
