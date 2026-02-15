package com.networknt.genai.model.audio;

import com.networknt.genai.Experimental;
import com.networknt.genai.data.audio.Audio;

/**
 * Request to transcribe audio.
 */
@Experimental
public class AudioTranscriptionRequest {

    private final Audio audio;
    private final String prompt;
    private final String language;
    private final Double temperature;

    private AudioTranscriptionRequest(Builder builder) {
        this.audio = builder.audio;
        this.prompt = builder.prompt;
        this.language = builder.language;
        this.temperature = builder.temperature;
    }

    /**
     * Retrieves the audio data to transcribe.
     *
     * @return Audio data to transcribe
     */
    public Audio audio() {
        return audio;
    }

    /**
     * Retrieves the prompt.
     *
     * @return An optional prompt to guide the model's transcription
     */
    public String prompt() {
        return prompt;
    }

    /**
     * Retrieves the language.
     *
     * @return An optional language code to use for the transcription
     */
    public String language() {
        return language;
    }

    /**
     * Retrieves the temperature.
     *
     * @return An optional temperature parameter for the transcription (0.0-1.0)
     */
    public Double temperature() {
        return temperature;
    }

    /**
     * Creates a new builder.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new builder with the given audio.
     *
     * @param audio the audio
     * @return a new builder
     */
    public static Builder builder(Audio audio) {
        return new Builder().audio(audio);
    }

    /**
     * Builder for {@link AudioTranscriptionRequest}.
     */
    public static class Builder {
        private Audio audio;
        private String prompt;
        private String language;
        private Double temperature;

        /**
         * Default constructor.
         */
        public Builder() {}

        /**
         * Sets the audio data to transcribe.
         *
         * @param audio The audio data
         * @return builder
         */
        public Builder audio(Audio audio) {
            this.audio = audio;
            return this;
        }

        /**
         * Sets an optional text prompt to guide the model's transcription.
         *
         * @param prompt The text prompt
         * @return builder
         */
        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        /**
         * Sets the language of the audio for more accurate transcription.
         *
         * @param language The language code (e.g., "en", "fr", "es")
         * @return builder
         */
        public Builder language(String language) {
            this.language = language;
            return this;
        }

        /**
         * Sets the temperature parameter for controlling randomness in the transcription.
         *
         * @param temperature A value between 0.0 and 1.0
         * @return builder
         */
        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        /**
         * Builds the request.
         *
         * @return the request
         */
        public AudioTranscriptionRequest build() {
            if (audio == null) {
                throw new IllegalStateException("Audio must be provided");
            }
            return new AudioTranscriptionRequest(this);
        }
    }
}
