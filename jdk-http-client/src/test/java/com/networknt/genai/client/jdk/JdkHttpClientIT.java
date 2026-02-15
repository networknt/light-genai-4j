package com.networknt.genai.client.jdk;

import com.networknt.genai.client.HttpClient;
import com.networknt.genai.client.HttpClientIT;
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
