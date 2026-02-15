package com.networknt.genai.model.language;

import com.networknt.genai.model.ModelDisabledException;
import com.networknt.genai.model.StreamingResponseHandler;
import com.networknt.genai.model.input.Prompt;

/**
 * A {@link StreamingLanguageModel} which throws a {@link ModelDisabledException} for all of its methods
 * <p>
 *     This could be used in tests, or in libraries that extend this one to conditionally enable or disable functionality.
 * </p>
 */
public class DisabledStreamingLanguageModel implements StreamingLanguageModel {

    /**
     * Creates a new instance.
     */
    public DisabledStreamingLanguageModel() {
    }
    @Override
    public void generate(String prompt, StreamingResponseHandler<String> handler) {
        throw new ModelDisabledException("StreamingLanguageModel is disabled");
    }

    @Override
    public void generate(Prompt prompt, StreamingResponseHandler<String> handler) {
        throw new ModelDisabledException("StreamingLanguageModel is disabled");
    }
}
