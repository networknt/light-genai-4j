package com.networknt.genai.gemini;

import com.networknt.config.Config;
import com.networknt.config.schema.ConfigSchema;
import com.networknt.config.schema.OutputFormat;
import com.networknt.config.schema.StringField;
import com.networknt.server.ModuleRegistry;

import java.util.Map;

@ConfigSchema(configKey = "gemini", configName = "gemini", configDescription = "Gemini GenAI configuration", outputFormats = {
        OutputFormat.JSON_SCHEMA, OutputFormat.YAML, OutputFormat.CLOUD })
public class GeminiConfig {
    public static final String CONFIG_NAME = "gemini";
    private static final String URL = "url";
    private static final String MODEL = "model";
    private static final String API_KEY = "apiKey";

    @StringField(configFieldName = URL, externalizedKeyName = URL, description = "Gemini API URL")
    private String url;

    @StringField(configFieldName = MODEL, externalizedKeyName = MODEL, description = "Model Name")
    private String model;

    @StringField(configFieldName = API_KEY, externalizedKeyName = API_KEY, description = "API Key")
    private String apiKey;

    private static volatile GeminiConfig instance;
    private final Map<String, Object> mappedConfig;

    private GeminiConfig(String configName) {
        mappedConfig = Config.getInstance().getJsonMapConfig(configName);
        setConfigData();
    }

    private GeminiConfig() {
        this(CONFIG_NAME);
    }

    public static GeminiConfig load() {
        return load(CONFIG_NAME);
    }

    public static GeminiConfig load(String configName) {
        GeminiConfig config = instance;
        if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
            synchronized (GeminiConfig.class) {
                config = instance;
                if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
                    config = new GeminiConfig(configName);
                    instance = config;
                    ModuleRegistry.registerModule(configName, GeminiConfig.class.getName(),
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
        } else {
            // Default if config is missing completely? Unlikely. But if so:
            url = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";
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
