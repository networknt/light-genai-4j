package com.networknt.agent.spi.json;

import com.networknt.agent.Internal;
import com.networknt.agent.internal.Json;

/**
 * A factory for creating {@link Json.JsonCodec} instances through SPI.
 */
@Internal
public interface JsonCodecFactory {

    /**
     * Create a new {@link Json.JsonCodec}.
     * @return the new {@link Json.JsonCodec}.
     */
    Json.JsonCodec create();
}
