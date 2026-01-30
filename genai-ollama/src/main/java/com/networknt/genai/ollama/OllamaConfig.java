package com.networknt.genai.ollama;

import com.networknt.config.Config;
import java.util.Map;

public class OllamaConfig {
    public static final String CONFIG_NAME = "ollama";
    private String ollamaUrl;
    private String model;

    private static final String OLLAMA_URL = "ollamaUrl";
    private static final String MODEL = "model";

    private final Config config;
    private Map<String, Object> mappedConfig;

    private OllamaConfig() {
        this(Config.getInstance());
    }

    private OllamaConfig(Config config) {
        this.config = config;
        this.mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigData();
    }

    public static OllamaConfig load() {
        return new OllamaConfig();
    }

    public void reload() {
        mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigData();
    }

    private void setConfigData() {
        Object object = mappedConfig.get(OLLAMA_URL);
        if (object != null) {
            ollamaUrl = (String) object;
        }
        object = mappedConfig.get(MODEL);
        if (object != null) {
            model = (String) object;
        }
    }

    public String getOllamaUrl() {
        return ollamaUrl;
    }

    public void setOllamaUrl(String ollamaUrl) {
        this.ollamaUrl = ollamaUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
