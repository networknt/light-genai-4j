package com.networknt.genai.gemini;

import com.networknt.config.Config;
import java.util.Map;

public class GeminiConfig {
    public static final String CONFIG_NAME = "gemini";
    private String url;
    private String model;
    private String apiKey;

    private static final String URL = "url";
    private static final String MODEL = "model";
    private static final String API_KEY = "apiKey";

    private final Config config;
    private Map<String, Object> mappedConfig;

    private GeminiConfig() {
        this(Config.getInstance());
    }

    private GeminiConfig(Config config) {
        this.config = config;
        this.mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigData();
    }

    public static GeminiConfig load() {
        return new GeminiConfig();
    }

    public void reload() {
        mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigData();
    }

    private void setConfigData() {
        Object object = mappedConfig.get(URL);
        if (object != null) {
            url = (String) object;
        } else {
            // Default Google Vertex AI / AI Studio specific URL structure (template)
            url = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";
        }
        object = mappedConfig.get(MODEL);
        if (object != null) {
            model = (String) object;
        }
        object = mappedConfig.get(API_KEY);
        if (object != null) {
            apiKey = (String) object;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
