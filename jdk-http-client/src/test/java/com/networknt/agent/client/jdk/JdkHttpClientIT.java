package com.networknt.agent.client.jdk;

import com.networknt.agent.client.HttpClient;
import com.networknt.agent.client.HttpClientIT;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class JdkHttpClientIT extends HttpClientIT {

    @Override
    protected List<HttpClient> clients() {
        return List.of(
                JdkHttpClient.builder().build()
        );
    }
}
