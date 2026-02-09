package com.networknt.agent.rag.content.retriever;

import com.networknt.agent.rag.content.Content;
import com.networknt.agent.rag.query.Query;
import com.networknt.agent.web.search.WebSearchEngine;
import com.networknt.agent.web.search.WebSearchRequest;
import com.networknt.agent.web.search.WebSearchResults;

import java.util.List;

import static com.networknt.agent.internal.Utils.getOrDefault;
import static com.networknt.agent.internal.ValidationUtils.ensureNotNull;
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

    public WebSearchContentRetriever(WebSearchEngine webSearchEngine, Integer maxResults) {
        this.webSearchEngine = ensureNotNull(webSearchEngine, "webSearchEngine");
        this.maxResults = getOrDefault(maxResults, 5);
    }

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

    public static class WebSearchContentRetrieverBuilder {
        private WebSearchEngine webSearchEngine;
        private Integer maxResults;

        WebSearchContentRetrieverBuilder() {
        }

        public WebSearchContentRetrieverBuilder webSearchEngine(WebSearchEngine webSearchEngine) {
            this.webSearchEngine = webSearchEngine;
            return this;
        }

        public WebSearchContentRetrieverBuilder maxResults(Integer maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        public WebSearchContentRetriever build() {
            return new WebSearchContentRetriever(this.webSearchEngine, this.maxResults);
        }
    }
}
