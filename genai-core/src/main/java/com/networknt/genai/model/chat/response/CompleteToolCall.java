package com.networknt.genai.model.chat.response;

import static com.networknt.genai.internal.ValidationUtils.ensureNotNegative;
import static com.networknt.genai.internal.ValidationUtils.ensureNotNull;

import com.networknt.genai.Experimental;
import com.networknt.genai.tool.ToolExecutionRequest;
import com.networknt.genai.internal.JacocoIgnoreCoverageGenerated;

import java.util.Objects;

/**
 * Represents a complete tool call.
 * Includes the index, and complete {@link ToolExecutionRequest}.
 *
 * @see PartialToolCall
 * @since 1.2.0
 */
@Experimental
@JacocoIgnoreCoverageGenerated
public class CompleteToolCall {

    private final int index;
    private final ToolExecutionRequest toolExecutionRequest;

    /**
     * Creates a new complete tool call.
     *
     * @param index the index
     * @param toolExecutionRequest the tool execution request
     */
    public CompleteToolCall(int index, ToolExecutionRequest toolExecutionRequest) {
        this.index = ensureNotNegative(index, "index");
        this.toolExecutionRequest = ensureNotNull(toolExecutionRequest, "toolExecutionRequest");
    }

    /**
     * The index of the tool call, starting from 0 and increasing by 1.
     * When the LLM initiates multiple tool calls, this index helps correlate
     * the different {@link PartialToolCall}s with each other and with the final {@link CompleteToolCall}.
     *
     * @return the index
     */
    public int index() {
        return index;
    }

    /**
     * A fully constructed {@link ToolExecutionRequest} that is ready for execution.
     *
     * @return the tool execution request
     */
    public ToolExecutionRequest toolExecutionRequest() {
        return toolExecutionRequest;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CompleteToolCall that = (CompleteToolCall) object;
        return index == that.index && Objects.equals(toolExecutionRequest, that.toolExecutionRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, toolExecutionRequest);
    }

    @Override
    public String toString() {
        return "CompleteToolCall{" + "index=" + index + ", toolExecutionRequest=" + toolExecutionRequest + '}';
    }
}
