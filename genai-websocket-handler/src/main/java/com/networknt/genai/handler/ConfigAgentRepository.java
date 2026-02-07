package com.networknt.genai.handler;

import com.networknt.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigAgentRepository implements AgentRepository {
    private static final Logger logger = LoggerFactory.getLogger(ConfigAgentRepository.class);
    private static final String CONFIG_NAME = "agents";
    
    private final Map<String, AgentDefinition> agents = new HashMap<>();

    public ConfigAgentRepository() {
        Map<String, Object> config = Config.getInstance().getJsonMapConfig(CONFIG_NAME);
        if (config != null) {
            List<Map<String, Object>> agentsList = (List<Map<String, Object>>) config.get("agents");
            if (agentsList != null) {
                for (Map<String, Object> agentMap : agentsList) {
                    AgentDefinition agent = new AgentDefinition();
                    agent.setAgentId((String) agentMap.get("agentId"));
                    agent.setModel((String) agentMap.get("model"));
                    agent.setSystemPrompt((String) agentMap.get("systemPrompt"));
                    agent.setDescription((String) agentMap.get("description"));
                    agent.setTools((List<String>) agentMap.get("tools"));
                    
                    agents.put(agent.getAgentId(), agent);
                    logger.info("Loaded agent: {}", agent.getAgentId());
                }
            }
        } else {
            logger.warn("No agents.yml config found.");
        }
    }

    @Override
    public AgentDefinition getAgent(String agentId) {
        return agents.get(agentId);
    }
}
