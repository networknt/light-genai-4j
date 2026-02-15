package com.networknt.genai.executor.ployglot;

import com.networknt.genai.tool.P;
import com.networknt.genai.tool.Tool;
import com.networknt.genai.code.CodeExecutionEngine;


/**
 * A tool that executes provided JavaScript code using GraalVM Polyglot/Truffle.
 * Attention! It might be dangerous to execute the code, see {@link GraalVmJavaScriptExecutionEngine} for more details.
 */
public class GraalVmJavaScriptExecutionTool {

    private final CodeExecutionEngine engine = new GraalVmJavaScriptExecutionEngine();

    @Tool("MUST be used for accurate calculations: math, sorting, filtering, aggregating, string processing, etc")
    public String executeJavaScriptCode(@P("JavaScript code to execute, result MUST be returned by the code") String code) {
        return engine.execute(code);
    }
}
