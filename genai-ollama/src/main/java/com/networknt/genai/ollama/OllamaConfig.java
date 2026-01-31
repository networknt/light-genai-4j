package com.networknt.genai.ollama;

import com.networknt.config.Config;
import com.networknt.config.schema.ConfigSchema;
import com.networknt.config.schema.OutputFormat;
import com.networknt.config.schema.StringField;
import com.networknt.server.ModuleRegistry;

import java.util.Map;

@ConfigSchema(configKey = "ollama", configName = "ollama", configDescription = "Ollama GenAI configuration", outputFormats = {
        OutputFormat.JSON_SCHEMA, OutputFormat.YAML, OutputFormat.CLOUD })
public class OllamaConfig {
    public static final String CONFIG_NAME = "ollama";
    private static final String OLLAMA_URL = "ollamaUrl";
    private static final String MODEL = "model";

    @StringField(configFieldName = OLLAMA_URL, externalizedKeyName = OLLAMA_URL, description = "Ollama API URL")
    private String ollamaUrl;

    @StringField(configFieldName = MODEL, externalizedKeyName = MODEL, description = "Model Name")
    private String model;

    private static volatile OllamaConfig instance;
    private final Map<String, Object> mappedConfig;

    private OllamaConfig(String configName) {
        mappedConfig = Config.getInstance().getJsonMapConfig(configName);
        setConfigData();
    }

    private OllamaConfig() {
        this(CONFIG_NAME);
    }

    public static OllamaConfig load() {
        return load(CONFIG_NAME);
    }

    public static OllamaConfig load(String configName) {
        OllamaConfig config = instance;
        if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
            synchronized (OllamaConfig.class) {
                config = instance;
                if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
                    config = new OllamaConfig(configName);
                    instance = config;
                    ModuleRegistry.registerModule(configName, OllamaConfig.class.getName(),
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
            Object object = mappedConfig.get(OLLAMA_URL);
            if (object != null) {
                ollamaUrl = (String) object;
            }
            object = mappedConfig.get(MODEL);
            if (object != null) {
                model = (String) object;
            }
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
