package com.networknt.genai.executor.ployglot;

import com.networknt.agent.tool.P;
import com.networknt.agent.tool.Tool;
import com.networknt.agent.code.CodeExecutionEngine;


/**
 * A tool that executes provided Python code using GraalVM Polyglot/Truffle.
 * Attention! It might be dangerous to execute the code, see {@link GraalVmPythonExecutionEngine} for more details.
 */
public class GraalVmPythonExecutionTool {

    private final CodeExecutionEngine engine = new GraalVmPythonExecutionEngine();

    @Tool("MUST be used for accurate calculations: math, sorting, filtering, aggregating, string processing, etc")
    public String executePythonCode(@P("Python code to execute, result MUST be returned by the code") String code) {
        return engine.execute(code);
    }
}
