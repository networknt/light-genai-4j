package com.networknt.model.ollama;

import static dev.langchain4j.internal.Utils.isNullOrEmpty;

import com.github.dockerjava.api.command.InspectContainerResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

public class LC4jOllamaContainer extends OllamaContainer {

    private static final Logger log = LoggerFactory.getLogger(LC4jOllamaContainer.class);

    private List<String> models;

    public LC4jOllamaContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        this.models = new ArrayList<>();
        // Fix for "libnvidia-ml.so.1: cannot open shared object file" error
        this.withCreateContainerCmdModifier(cmd -> {
            if (cmd.getHostConfig() != null) {
                cmd.getHostConfig().withDeviceRequests(null);
            }
        });
    }

    public LC4jOllamaContainer withModel(String model) {
        this.models.add(model);
        return this;
    }

    public LC4jOllamaContainer withModels(List<String> models) {
        this.models = models;
        return this;
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        if (!isNullOrEmpty(models)) {
            for (String model : models) {
                pullModelIfNotPresent(model);
            }
        }
    }
    
    private void pullModelIfNotPresent(String model) {
        try {
            if (isModelAvailable(model)) {
                log.info("Model '{}' is already available in the container", model);
                return;
            }
            
            log.info("Start pulling the '{}' model ... would take several minutes ...", model);
            ExecResult pullResult = execInContainer("ollama", "pull", model);
            if (pullResult.getExitCode() != 0) {
                throw new RuntimeException("Error pulling model " + model + ". Exit code: " + pullResult.getExitCode() + ". Stdout: " + pullResult.getStdout() + ". Stderr: " + pullResult.getStderr());
            }
            log.info("Model pulling completed! {}", pullResult);
            
            if (!isModelAvailable(model)) {
                throw new RuntimeException("Model " + model + " was pulled but is not available. 'ollama list' output did not contain the model name.");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error pulling model", e);
        }
    }
    
    private boolean isModelAvailable(String model) throws IOException, InterruptedException {
        ExecResult result = execInContainer("ollama", "list");
        return result.getExitCode() == 0 && result.getStdout().contains(model);
    }
}
