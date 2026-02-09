package com.networknt.agent.model.language;

import com.networknt.agent.model.ModelDisabledException;
import com.networknt.agent.model.input.Prompt;
import com.networknt.agent.model.output.Response;

/**
 * A {@link LanguageModel} which throws a {@link ModelDisabledException} for all of its methods
 * <p>
 *     This could be used in tests, or in libraries that extend this one to conditionally enable or disable functionality.
 * </p>
 */
public class DisabledLanguageModel implements LanguageModel {
    @Override
    public Response<String> generate(String prompt) {
        throw new ModelDisabledException("LanguageModel is disabled");
    }

    @Override
    public Response<String> generate(Prompt prompt) {
        throw new ModelDisabledException("LanguageModel is disabled");
    }
}
