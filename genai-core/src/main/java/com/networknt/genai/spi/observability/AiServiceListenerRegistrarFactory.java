package com.networknt.genai.spi.observability;

import com.networknt.genai.observability.api.AiServiceListenerRegistrar;
import java.util.function.Supplier;

/**
 * A factory for creating {@link AiServiceListenerRegistrar} instances.
 */
public interface AiServiceListenerRegistrarFactory extends Supplier<AiServiceListenerRegistrar> {}
