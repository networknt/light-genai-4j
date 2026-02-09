package com.networknt.agent.service.output;

import com.networknt.agent.Internal;
import com.networknt.agent.model.chat.request.json.JsonSchema;

import java.util.Optional;

/**
 * Represents an output parser.
 *
 * @param <T> the type of the output.
 */
@Internal
interface OutputParser<T> {

    /**
     * Parse the given text.
     *
     * @param text the text to parse.
     * @return the parsed output.
     */
    T parse(String text);

    /**
     * JSON schema of the type.
     *
     * @return the JSON schema, if supported.
     */
    default Optional<JsonSchema> jsonSchema() {
        return Optional.empty();
    }

    /**
     * Description of the text format.
     *
     * @return the description of the text format.
     */
    String formatInstructions();
}
