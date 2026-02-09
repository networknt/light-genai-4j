package com.networknt.agent.spi.observability;

import com.networknt.agent.observability.api.AiServiceListenerRegistrar;
import java.util.function.Supplier;

/**
 * A factory for creating {@link AiServiceListenerRegistrar} instances.
 */
public interface AiServiceListenerRegistrarFactory extends Supplier<AiServiceListenerRegistrar> {}
