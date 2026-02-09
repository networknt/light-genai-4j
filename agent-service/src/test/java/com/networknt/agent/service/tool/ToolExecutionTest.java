package com.networknt.agent.service.tool;

import static org.assertj.core.api.Assertions.assertThat;

import com.networknt.agent.tool.ToolExecutionRequest;
import org.junit.jupiter.api.Test;

class ToolExecutionTest {

    @Test
    void test_deprecated_result_setter() {

        String textResult = "text result";

        ToolExecution toolExecution = ToolExecution.builder()
                .request(ToolExecutionRequest.builder().build())
                .result(textResult)
                .build();

        assertThat(toolExecution.result()).isEqualTo(textResult);
        assertThat(toolExecution.resultObject()).isNull();
    }
}
