package com.networknt.genai.model.anthropic.common;

import com.networknt.genai.model.ModelProvider;
import com.networknt.genai.model.anthropic.AnthropicModelCatalog;
import com.networknt.genai.model.catalog.AbstractModelCatalogIT;
import com.networknt.genai.model.catalog.ModelCatalog;
import com.networknt.genai.model.catalog.ModelDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static com.networknt.genai.model.ModelProvider.ANTHROPIC;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class AnthropicModelCatalogIT extends AbstractModelCatalogIT {

    @Override
    protected ModelCatalog createModelCatalog() {
        return AnthropicModelCatalog.builder()
                .apiKey(System.getenv("ANTHROPIC_API_KEY"))
                .build();
    }

    @Override
    protected ModelProvider expectedProvider() {
        return ANTHROPIC;
    }

    @Test
    void should_have_creation_timestamp() {
        ModelCatalog catalog = createModelCatalog();

        List<ModelDescription> models = catalog.listModels();

        assertThat(models).isNotEmpty();
        assertThat(models).anyMatch(m -> m.createdAt() != null);
    }
}
