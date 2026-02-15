package com.networknt.genai.internal;

import com.networknt.genai.Internal;
import com.networknt.genai.exception.AuthenticationException;
import com.networknt.genai.exception.HttpException;
import com.networknt.genai.exception.InternalServerException;
import com.networknt.genai.exception.InvalidRequestException;
import com.networknt.genai.exception.LangChain4jException;
import com.networknt.genai.exception.ModelNotFoundException;
import com.networknt.genai.exception.RateLimitException;
import com.networknt.genai.exception.TimeoutException;
import com.networknt.genai.exception.UnresolvedModelServerException;

import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.Callable;

/**
 * Interface for mapping exceptions.
 */
@Internal
@FunctionalInterface
public interface ExceptionMapper {

    /**
     * The default exception mapper.
     */
    ExceptionMapper DEFAULT = new DefaultExceptionMapper();

    /**
     * Maps the exception from the action.
     *
     * @param action the action
     * @param <T> the result type
     * @return the result
     */
    static <T> T mappingException(Callable<T> action) {
        return DEFAULT.withExceptionMapper(action);
    }

    /**
     * Executes the action with exception mapping.
     *
     * @param action the action
     * @param <T> the result type
     * @return the result
     */
    default <T> T withExceptionMapper(Callable<T> action) {
        try {
            return action.call();
        } catch (Exception e) {
            throw mapException(e);
        }
    }

    /**
     * Maps the throwable to a runtime exception.
     *
     * @param t the throwable
     * @return the runtime exception
     */
    RuntimeException mapException(Throwable t);

    /**
     * The default exception mapper implementation.
     */
    class DefaultExceptionMapper implements ExceptionMapper {

        /**
         * Creates a new default exception mapper.
         */
        public DefaultExceptionMapper() {}

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

        /**
         * Maps the http status code to a runtime exception.
         *
         * @param cause the cause
         * @param httpStatusCode the http status code
         * @return the runtime exception
         */
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
