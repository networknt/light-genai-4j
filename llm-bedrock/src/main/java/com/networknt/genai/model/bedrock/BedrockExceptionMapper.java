package com.networknt.genai.model.bedrock;

import com.networknt.genai.Internal;
import com.networknt.genai.exception.LangChain4jException;
import com.networknt.genai.exception.TimeoutException;
import com.networknt.genai.internal.ExceptionMapper;
import software.amazon.awssdk.core.exception.SdkServiceException;

@Internal
class BedrockExceptionMapper extends ExceptionMapper.DefaultExceptionMapper {

    static final BedrockExceptionMapper INSTANCE = new BedrockExceptionMapper();

    @Override
    public RuntimeException mapException(Throwable t) {

        if (t instanceof SdkServiceException sdkServiceException) {
            return mapHttpStatusCode(sdkServiceException, sdkServiceException.statusCode());
        } else if (t.getCause() instanceof SdkServiceException sdkServiceException) {
            return mapHttpStatusCode(sdkServiceException, sdkServiceException.statusCode());
        }

        if (t instanceof software.amazon.awssdk.core.exception.ApiCallTimeoutException) {
            return new TimeoutException(t);
        } else if (t.getCause() instanceof software.amazon.awssdk.core.exception.ApiCallTimeoutException) {
            return new TimeoutException(t.getCause());
        }

        return t instanceof RuntimeException re ? re : new LangChain4jException(t);
    }
}
