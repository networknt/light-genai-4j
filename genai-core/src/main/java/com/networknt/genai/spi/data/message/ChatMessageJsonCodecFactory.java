package com.networknt.genai.spi.data.message;

import com.networknt.genai.Internal;
import com.networknt.genai.data.message.ChatMessageJsonCodec;

/**
 * A factory for creating {@link ChatMessageJsonCodec} objects.
 * Used for SPI.
 */
@Internal
public interface ChatMessageJsonCodecFactory {

    /**
     * Creates a new {@link ChatMessageJsonCodec} object.
     * @return the new {@link ChatMessageJsonCodec} object.
     */
    ChatMessageJsonCodec create();
}
