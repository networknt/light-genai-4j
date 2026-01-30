package com.networknt.genai.openai;

import com.networknt.config.Config;
import java.util.Map;

public class OpenAiConfig {
    public static final String CONFIG_NAME = "openai";
    private String url;
    private String model;
    private String apiKey;

    private static final String URL = "url";
    private static final String MODEL = "model";
    private static final String API_KEY = "apiKey";

    private final Config config;
    private Map<String, Object> mappedConfig;

    private OpenAiConfig() {
        this(Config.getInstance());
    }

    private OpenAiConfig(Config config) {
        this.config = config;
        this.mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigData();
    }

    public static OpenAiConfig load() {
        return new OpenAiConfig();
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
            // Default OpenAI API URL
            url = "https://api.openai.com/v1/chat/completions";
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
