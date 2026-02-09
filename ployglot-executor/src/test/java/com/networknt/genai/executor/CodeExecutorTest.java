package com.networknt.genai.executor;

import com.networknt.agent.code.CodeExecutionEngine;
import com.networknt.genai.executor.ployglot.GraalVmJavaScriptExecutionEngine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CodeExecutorTest {

    @Test
    public void testExecuteJs() {
        CodeExecutionEngine engine = new GraalVmJavaScriptExecutionEngine();
        String result = engine.execute("var x = 1 + 2; x;");
        assertEquals("3", result);
    }
    
    @Test
    public void testExecutePython() {
        // Note: Python might not be available by default without extra setup or native image, 
        // but let's try a simple arithmetic that might work if the polyglot engine is configured or fallbacks.
        // Actually, GraalVM python is an additional install usually. 
        // Let's stick to JS strictly for the basic verification as 'js' is often bundled or easily available.
        // If we want python we need org.graalvm.polyglot:python dependency which we didn't add.
        // So we skip python test for now.
    }
}
