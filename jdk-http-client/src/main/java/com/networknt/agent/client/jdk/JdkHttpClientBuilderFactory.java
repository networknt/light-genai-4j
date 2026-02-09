package com.networknt.agent.client.jdk;

import com.networknt.agent.client.HttpClientBuilderFactory;

public class JdkHttpClientBuilderFactory implements HttpClientBuilderFactory {

    @Override
    public JdkHttpClientBuilder create() {
        return JdkHttpClient.builder();
    }
}
