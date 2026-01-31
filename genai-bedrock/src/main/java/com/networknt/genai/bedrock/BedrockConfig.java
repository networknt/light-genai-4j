package com.networknt.genai.bedrock;

import com.networknt.config.Config;
import com.networknt.config.schema.ConfigSchema;
import com.networknt.config.schema.OutputFormat;
import com.networknt.config.schema.StringField;
import com.networknt.server.ModuleRegistry;

import java.util.Map;

@ConfigSchema(configKey = "bedrock", configName = "bedrock", configDescription = "Bedrock GenAI configuration", outputFormats = {
        OutputFormat.JSON_SCHEMA, OutputFormat.YAML, OutputFormat.CLOUD })
public class BedrockConfig {
    public static final String CONFIG_NAME = "bedrock";
    private static final String REGION = "region";
    private static final String MODEL_ID = "modelId";

    @StringField(configFieldName = REGION, externalizedKeyName = REGION, description = "AWS Region")
    private String region;

    @StringField(configFieldName = MODEL_ID, externalizedKeyName = MODEL_ID, description = "Model ID")
    private String modelId;

    private static volatile BedrockConfig instance;
    private final Map<String, Object> mappedConfig;

    private BedrockConfig(String configName) {
        mappedConfig = Config.getInstance().getJsonMapConfig(configName);
        setConfigData();
    }

    private BedrockConfig() {
        this(CONFIG_NAME);
    }

    public static BedrockConfig load() {
        return load(CONFIG_NAME);
    }

    public static BedrockConfig load(String configName) {
        BedrockConfig config = instance;
        if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
            synchronized (BedrockConfig.class) {
                config = instance;
                if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
                    config = new BedrockConfig(configName);
                    instance = config;
                    ModuleRegistry.registerModule(configName, BedrockConfig.class.getName(),
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
            Object object = mappedConfig.get(REGION);
            if (object != null) {
                region = (String) object;
            }
            object = mappedConfig.get(MODEL_ID);
            if (object != null) {
                modelId = (String) object;
            }
        }
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }
}
