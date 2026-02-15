package com.networknt.genai.model.audio;

import com.networknt.genai.Experimental;
import com.networknt.genai.data.audio.Audio;
import com.networknt.genai.model.ModelProvider;

import static com.networknt.genai.model.ModelProvider.OTHER;

/**
 * A model that can transcribe audio into text.
 */
@Experimental
public interface AudioTranscriptionModel {

    /**
     * Given an audio transcription request, generates a transcription.
     *
     * @param request The transcription request containing the audio file and optional parameters
     * @return The generated transcription response
     */
    AudioTranscriptionResponse transcribe(AudioTranscriptionRequest request);

    /**
     * Convenience method for simple transcription needs.
     * Given an audio file, generates a transcription.
     *
     * @param audio The audio file to generate a transcription from
     * @return The generated transcription as a plain string
     */
    default String transcribeToText(Audio audio) {
        AudioTranscriptionRequest request =
                AudioTranscriptionRequest.builder(audio).build();
        AudioTranscriptionResponse response = transcribe(request);
        return response.text();
    }

    /**
     * @return the model provider
     */
    default ModelProvider provider() {
        return OTHER;
    }
}
