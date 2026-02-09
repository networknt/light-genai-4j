package com.networknt.agent.client.jdk;

import com.networknt.agent.client.HttpClient;
import com.networknt.agent.client.HttpClientTimeoutIT;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;

class JdkHttpClientTimeoutIT extends HttpClientTimeoutIT {

    @Override
    protected List<HttpClient> clients(Duration readTimeout) {
        return List.of(
                JdkHttpClient.builder()
                        .readTimeout(readTimeout)
                        .build()
        );
    }

    @Override
    protected Class<? extends Exception> expectedReadTimeoutRootCauseExceptionType() {
        return HttpTimeoutException.class;
    }
}
