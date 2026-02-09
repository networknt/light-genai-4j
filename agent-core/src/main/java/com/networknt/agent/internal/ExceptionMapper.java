package com.networknt.agent.internal;

import com.networknt.agent.Internal;
import com.networknt.agent.exception.AuthenticationException;
import com.networknt.agent.exception.HttpException;
import com.networknt.agent.exception.InternalServerException;
import com.networknt.agent.exception.InvalidRequestException;
import com.networknt.agent.exception.LangChain4jException;
import com.networknt.agent.exception.ModelNotFoundException;
import com.networknt.agent.exception.RateLimitException;
import com.networknt.agent.exception.TimeoutException;
import com.networknt.agent.exception.UnresolvedModelServerException;

import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.Callable;

@Internal
@FunctionalInterface
public interface ExceptionMapper {

    ExceptionMapper DEFAULT = new DefaultExceptionMapper();

    static <T> T mappingException(Callable<T> action) {
        return DEFAULT.withExceptionMapper(action);
    }

    default <T> T withExceptionMapper(Callable<T> action) {
        try {
            return action.call();
        } catch (Exception e) {
            throw mapException(e);
        }
    }

    RuntimeException mapException(Throwable t);

    class DefaultExceptionMapper implements ExceptionMapper {

        @Override
        public RuntimeException mapException(Throwable t) {
            Throwable rootCause = findRoot(t);

            if (rootCause instanceof HttpException httpException) {
                return mapHttpStatusCode(httpException, httpException.statusCode());
            }

            if (rootCause instanceof UnresolvedAddressException) {
                return new UnresolvedModelServerException(rootCause);
            }

            return t instanceof RuntimeException re ? re : new LangChain4jException(t);
        }

        protected RuntimeException mapHttpStatusCode(Throwable cause, int httpStatusCode) {
            if (httpStatusCode >= 500 && httpStatusCode < 600) {
                return new InternalServerException(cause);
            }
            if (httpStatusCode == 401 || httpStatusCode == 403) {
                return new AuthenticationException(cause);
            }
            if (httpStatusCode == 404) {
                return new ModelNotFoundException(cause);
            }
            if (httpStatusCode == 408) {
                return new TimeoutException(cause);
            }
            if (httpStatusCode == 429) {
                return new RateLimitException(cause);
            }
            if (httpStatusCode >= 400 && httpStatusCode < 500) {
                return new InvalidRequestException(cause);
            }
            return cause instanceof RuntimeException re ? re : new LangChain4jException(cause);
        }

        private static Throwable findRoot(Throwable e) {
            Throwable cause = e.getCause();
            return cause == null || cause == e ? e : findRoot(cause);
        }
    }
}
