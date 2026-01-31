package com.networknt.genai.openai;

import com.networknt.config.Config;
import com.networknt.config.schema.ConfigSchema;
import com.networknt.config.schema.OutputFormat;
import com.networknt.config.schema.StringField;
import com.networknt.server.ModuleRegistry;

import java.util.Map;

@ConfigSchema(configKey = "openai", configName = "openai", configDescription = "OpenAI GenAI configuration", outputFormats = {
        OutputFormat.JSON_SCHEMA, OutputFormat.YAML, OutputFormat.CLOUD })
public class OpenAiConfig {
    public static final String CONFIG_NAME = "openai";
    private static final String URL = "url";
    private static final String MODEL = "model";
    private static final String API_KEY = "apiKey";

    @StringField(configFieldName = URL, externalizedKeyName = URL, description = "OpenAI API URL")
    private String url;

    @StringField(configFieldName = MODEL, externalizedKeyName = MODEL, description = "Model Name")
    private String model;

    @StringField(configFieldName = API_KEY, externalizedKeyName = API_KEY, description = "API Key")
    private String apiKey;

    private static volatile OpenAiConfig instance;
    private final Map<String, Object> mappedConfig;

    private OpenAiConfig(String configName) {
        mappedConfig = Config.getInstance().getJsonMapConfig(configName);
        setConfigData();
    }

    private OpenAiConfig() {
        this(CONFIG_NAME);
    }

    public static OpenAiConfig load() {
        return load(CONFIG_NAME);
    }

    public static OpenAiConfig load(String configName) {
        OpenAiConfig config = instance;
        if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
            synchronized (OpenAiConfig.class) {
                config = instance;
                if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
                    config = new OpenAiConfig(configName);
                    instance = config;
                    ModuleRegistry.registerModule(configName, OpenAiConfig.class.getName(),
                            Config.getNoneDecryptedInstance().getJsonMapConfigNoCache(configName), null);
                }
            }
        }
        return config;
    }

    public Map<String, Object> getMappedConfig() {
        return mappedConfig;
    }

    private void setConfigData() {
        if (mappedConfig != null) {
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
        } else {
            // Default logic if config is missing logic
            url = "https://api.openai.com/v1/chat/completions";
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
