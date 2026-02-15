package com.networknt.genai.spi.json;

import com.networknt.genai.Internal;
import com.networknt.genai.internal.Json;

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
