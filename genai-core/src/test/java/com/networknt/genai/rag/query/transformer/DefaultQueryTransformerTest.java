package com.networknt.genai.rag.query.transformer;

import com.networknt.genai.rag.query.Query;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultQueryTransformerTest {

    @Test
    void should_return_same_query() {

        // given
        QueryTransformer transformer = new DefaultQueryTransformer();
        Query query = Query.from("query");

        // when
        Collection<Query> transformed = transformer.transform(query);

        // then
        assertThat(transformed).containsExactly(query);
    }
}