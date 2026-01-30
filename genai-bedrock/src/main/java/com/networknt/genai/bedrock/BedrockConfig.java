package com.networknt.genai.bedrock;

import com.networknt.config.Config;
import java.util.Map;

public class BedrockConfig {
    public static final String CONFIG_NAME = "bedrock";
    private String region;
    private String modelId;

    private static final String REGION = "region";
    private static final String MODEL_ID = "modelId";

    private final Config config;
    private Map<String, Object> mappedConfig;

    private BedrockConfig() {
        this(Config.getInstance());
    }

    private BedrockConfig(Config config) {
        this.config = config;
        this.mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigData();
    }

    public static BedrockConfig load() {
        return new BedrockConfig();
    }

    public void reload() {
        mappedConfig = config.getJsonMapConfigNoCache(CONFIG_NAME);
        setConfigData();
    }

    private void setConfigData() {
        Object object = mappedConfig.get(REGION);
        if (object != null) {
            region = (String) object;
        }
        object = mappedConfig.get(MODEL_ID);
        if (object != null) {
            modelId = (String) object;
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
