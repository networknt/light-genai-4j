package com.networknt.agent.model.embedding;

import com.networknt.agent.data.embedding.Embedding;
import com.networknt.agent.data.segment.TextSegment;
import com.networknt.agent.model.ModelDisabledException;
import com.networknt.agent.model.output.Response;

import java.util.List;

/**
 * An {@link EmbeddingModel} which throws a {@link ModelDisabledException} for all of its methods
 * <p>
 * This could be used in tests, or in libraries that extend this one to conditionally enable or disable functionality.
 * </p>
 */
public class DisabledEmbeddingModel implements EmbeddingModel {

    @Override
    public Response<Embedding> embed(String text) {
        throw new ModelDisabledException("EmbeddingModel is disabled");
    }

    @Override
    public Response<Embedding> embed(TextSegment textSegment) {
        throw new ModelDisabledException("EmbeddingModel is disabled");
    }

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        throw new ModelDisabledException("EmbeddingModel is disabled");
    }
}
