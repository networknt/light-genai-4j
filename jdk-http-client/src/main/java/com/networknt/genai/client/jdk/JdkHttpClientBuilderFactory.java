package com.networknt.genai.client.jdk;

import com.networknt.genai.client.HttpClientBuilderFactory;

public class JdkHttpClientBuilderFactory implements HttpClientBuilderFactory {

    @Override
    public JdkHttpClientBuilder create() {
        return JdkHttpClient.builder();
    }
}
