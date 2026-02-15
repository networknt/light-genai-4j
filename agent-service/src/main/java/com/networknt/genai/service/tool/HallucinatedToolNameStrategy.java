package com.networknt.genai.service.tool;

import static com.networknt.genai.internal.Exceptions.runtime;

import com.networknt.genai.tool.ToolExecutionRequest;
import com.networknt.genai.data.message.ToolExecutionResultMessage;
import java.util.function.Function;

public enum HallucinatedToolNameStrategy implements Function<ToolExecutionRequest, ToolExecutionResultMessage> {
    THROW_EXCEPTION;

    public ToolExecutionResultMessage apply(ToolExecutionRequest toolExecutionRequest) {
        switch (this) {
            case THROW_EXCEPTION -> {
                throw runtime(
                        "The LLM is trying to execute the '%s' tool, but no such tool exists. Most likely, it is a "
                                + "hallucination. You can override this default strategy by setting the hallucinatedToolNameStrategy on the AiService",
                        toolExecutionRequest.name());
            }
        }
        throw new UnsupportedOperationException();
    }
}
