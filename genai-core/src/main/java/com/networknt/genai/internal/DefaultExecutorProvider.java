package com.networknt.genai.internal;

import static com.networknt.genai.internal.VirtualThreadUtils.createVirtualThreadExecutor;

import com.networknt.genai.Internal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provides a default executor service.
 * <p>
 * This class is internal and should not be used directly.
 */
@Internal
public class DefaultExecutorProvider {

    private DefaultExecutorProvider() {}

    /**
     * Returns the default executor service.
     *
     * @return the default executor service
     */
    public static ExecutorService getDefaultExecutorService() {
        return Holder.EXECUTOR_SERVICE;
    }

    private static class Holder {
        private static final ExecutorService EXECUTOR_SERVICE =
                createVirtualThreadExecutor(Holder::createPlatformThreadExecutorService);

        private static ExecutorService createPlatformThreadExecutorService() {
            return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<>());
        }
    }
}
