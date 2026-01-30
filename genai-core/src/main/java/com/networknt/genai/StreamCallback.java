package com.networknt.genai;

public interface StreamCallback {
    /**
     * Called when a new chunk of content is received.
     * 
     * @param content The partial content string.
     */
    void onEvent(String content);

    /**
     * Called when the stream is complete.
     */
    void onComplete();

    /**
     * Called if an error occurs during streaming.
     * 
     * @param t The throwable error.
     */
    void onError(Throwable t);
}
