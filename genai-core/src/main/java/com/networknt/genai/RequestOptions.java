package com.networknt.genai;

/**
 * Options to customize a specific GenAI request, overriding global defaults.
 */
public class RequestOptions {
    private String model;
    private String systemPrompt;
    private Double temperature;

    public RequestOptions() {
    }

    public RequestOptions(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
