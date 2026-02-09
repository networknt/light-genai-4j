package com.networknt.agent.rag.content.retriever;

import com.networknt.agent.rag.content.Content;
import com.networknt.agent.rag.query.Query;
import com.networknt.agent.web.search.WebSearchEngine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class WebSearchContentRetrieverIT {

    protected abstract WebSearchEngine searchEngine();

    @Test
    void should_retrieve_web_page_as_content() {

        // given
        WebSearchContentRetriever contentRetriever = WebSearchContentRetriever.builder()
                .webSearchEngine(searchEngine())
                .build();

        Query query = Query.from("What is the current weather in New York?");

        // when
        List<Content> contents = contentRetriever.retrieve(query);

        // then
        assertThat(contents)
                .as("At least one content should be contains 'weather' and 'New York' ignoring case")
                .anySatisfy(content -> {
                            assertThat(content.textSegment().text())
                                    .containsIgnoringCase("weather")
                                    .containsIgnoringCase("New York");
                            assertThat(content.textSegment().metadata().getString("url"))
                                    .startsWith("https://");
                        }
                );
    }
}
