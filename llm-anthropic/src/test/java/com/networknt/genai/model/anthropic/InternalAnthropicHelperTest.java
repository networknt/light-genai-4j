package com.networknt.genai.model.anthropic;

import static com.networknt.genai.model.anthropic.InternalAnthropicHelper.validate;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.networknt.genai.exception.UnsupportedFeatureException;
import com.networknt.genai.model.chat.request.ChatRequestParameters;
import com.networknt.genai.model.chat.request.ResponseFormat;
import org.junit.jupiter.api.Test;

class InternalAnthropicHelperTest {

    @Test
    void validate_WithNoUnsupportedFeatures_ShouldNotThrowException() {
        // Given
        ChatRequestParameters parameters = ChatRequestParameters.builder()
                .responseFormat(ResponseFormat.TEXT)
                .build();

        // When-Then
        assertDoesNotThrow(() -> validate(parameters));
    }

    @Test
    void validate_WithSchemalessJsonResponseFormat_ShouldThrowException() {
        // Given
        ChatRequestParameters parameters = ChatRequestParameters.builder()
                .responseFormat(ResponseFormat.JSON)
                .build();

        // When
        UnsupportedFeatureException exception =
                assertThrows(UnsupportedFeatureException.class, () -> validate(parameters));

        // Then
        assertEquals(
                "Schemaless JSON response format is not supported by Anthropic",
                exception.getMessage());
    }

    @Test
    void validate_WithFrequencyPenalty_ShouldThrowException() {
        // Given
        ChatRequestParameters parameters = ChatRequestParameters.builder()
                .responseFormat(ResponseFormat.TEXT)
                .frequencyPenalty(0.5)
                .build();

        // When
        UnsupportedFeatureException exception =
                assertThrows(UnsupportedFeatureException.class, () -> validate(parameters));

        // Then
        assertEquals("Frequency Penalty is not supported by Anthropic", exception.getMessage());
    }

    @Test
    void validate_WithPresencePenalty_ShouldThrowException() {
        // Given
        ChatRequestParameters parameters = ChatRequestParameters.builder()
                .responseFormat(ResponseFormat.TEXT)
                .presencePenalty(0.5)
                .build();

        // When
        UnsupportedFeatureException exception =
                assertThrows(UnsupportedFeatureException.class, () -> validate(parameters));

        // Then
        assertEquals("Presence Penalty is not supported by Anthropic", exception.getMessage());
    }

    @Test
    void validate_WithTwoUnsupportedFeatures_ShouldThrowExceptionWithCombinedMessage() {
        // Given
        ChatRequestParameters parameters = ChatRequestParameters.builder()
                .presencePenalty(0.5)
                .frequencyPenalty(0.5)
                .build();

        // When
        UnsupportedFeatureException exception =
                assertThrows(UnsupportedFeatureException.class, () -> validate(parameters));

        // Then
        assertEquals(
                "Frequency Penalty, Presence Penalty are not supported by Anthropic",
                exception.getMessage());
    }
}
