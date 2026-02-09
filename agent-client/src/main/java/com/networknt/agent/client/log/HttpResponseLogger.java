package com.networknt.agent.client.log;

import static com.networknt.agent.client.log.HttpRequestLogger.format;

import com.networknt.agent.Internal;
import com.networknt.agent.client.SuccessfulHttpResponse;
import org.slf4j.Logger;

@Internal
class HttpResponseLogger {

    static void log(Logger log, SuccessfulHttpResponse response) {
        try {
            log.info(
                    """
                            HTTP response:
                            - status code: {}
                            - headers: {}
                            - body: {}
                            """,
                    response.statusCode(),
                    format(response.headers()),
                    response.body());
        } catch (Exception e) {
            log.warn("Exception occurred while logging HTTP response: {}", e.getMessage());
        }
    }
}
