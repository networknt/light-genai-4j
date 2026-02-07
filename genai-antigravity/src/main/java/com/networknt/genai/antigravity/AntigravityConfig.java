package com.networknt.genai.antigravity;

import com.networknt.config.Config;
import com.networknt.config.schema.ConfigSchema;
import com.networknt.config.schema.OutputFormat;
import com.networknt.config.schema.StringField;
import com.networknt.server.ModuleRegistry;

import java.util.Map;

@ConfigSchema(configKey = "antigravity", configName = "antigravity", configDescription = "Antigravity GenAI configuration", outputFormats = {
        OutputFormat.JSON_SCHEMA, OutputFormat.YAML, OutputFormat.CLOUD })
public class AntigravityConfig {
    public static final String CONFIG_NAME = "antigravity";
    private static final String URL = "url";
    private static final String MODEL = "model";
    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final String TOKEN_URL = "tokenUrl";
    private static final String AUTH_URL = "authUrl";
    private static final String REDIRECT_URI = "redirectUri";

    @StringField(configFieldName = URL, externalizedKeyName = URL, description = "Antigravity API URL", defaultValue = "https://cloudcode-pa.googleapis.com/v1internal:streamGenerateContent")
    private String url = "https://cloudcode-pa.googleapis.com/v1internal:streamGenerateContent";
    
    @StringField(configFieldName = MODEL, externalizedKeyName = MODEL, description = "Model ID", defaultValue = "google-antigravity/claude-opus-4-5-thinking")
    private String model = "google-antigravity/claude-opus-4-5-thinking";
    
    @StringField(configFieldName = CLIENT_ID, externalizedKeyName = CLIENT_ID, description = "OAuth Client ID", defaultValue = "1071006060591-tmhssin2h21lcre235vtolojh4g403ep.apps.googleusercontent.com")
    private String clientId = "1071006060591-tmhssin2h21lcre235vtolojh4g403ep.apps.googleusercontent.com";

    @StringField(configFieldName = CLIENT_SECRET, externalizedKeyName = CLIENT_SECRET, description = "OAuth Client Secret", defaultValue = "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")
    private String clientSecret = "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";

    @StringField(configFieldName = TOKEN_URL, externalizedKeyName = TOKEN_URL, description = "OAuth Token URL", defaultValue = "https://oauth2.googleapis.com/token")
    private String tokenUrl = "https://oauth2.googleapis.com/token";

    @StringField(configFieldName = AUTH_URL, externalizedKeyName = AUTH_URL, description = "OAuth Auth URL", defaultValue = "https://accounts.google.com/o/oauth2/v2/auth")
    private String authUrl = "https://accounts.google.com/o/oauth2/v2/auth";

    @StringField(configFieldName = REDIRECT_URI, externalizedKeyName = REDIRECT_URI, description = "OAuth Redirect URI", defaultValue = "http://localhost:51121/oauth-callback")
    private String redirectUri = "http://localhost:51121/oauth-callback";

    private static volatile AntigravityConfig instance;
    private final Map<String, Object> mappedConfig;

    private AntigravityConfig(String configName) {
        mappedConfig = Config.getInstance().getJsonMapConfig(configName);
        setConfigData();
    }

    private AntigravityConfig() {
        this(CONFIG_NAME);
    }

    public static AntigravityConfig load() {
        return load(CONFIG_NAME);
    }

    public static AntigravityConfig load(String configName) {
        AntigravityConfig config = instance;
        if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
            synchronized (AntigravityConfig.class) {
                config = instance;
                if (config == null || config.getMappedConfig() != Config.getInstance().getJsonMapConfig(configName)) {
                    config = new AntigravityConfig(configName);
                    instance = config;
                    ModuleRegistry.registerModule(configName, AntigravityConfig.class.getName(),
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
            if (object != null) url = (String) object;
            
            object = mappedConfig.get(MODEL);
            if (object != null) model = (String) object;
            
            object = mappedConfig.get(CLIENT_ID);
            if (object != null) clientId = (String) object;
            
            object = mappedConfig.get(CLIENT_SECRET);
            if (object != null) clientSecret = (String) object;
            
            object = mappedConfig.get(TOKEN_URL);
            if (object != null) tokenUrl = (String) object;

            object = mappedConfig.get(AUTH_URL);
            if (object != null) authUrl = (String) object;

            object = mappedConfig.get(REDIRECT_URI);
            if (object != null) redirectUri = (String) object;
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

    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }
    
    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
