package com.networknt.genai.model.openai.common;

import com.networknt.genai.model.ModelProvider;
import com.networknt.genai.model.catalog.AbstractModelCatalogIT;
import com.networknt.genai.model.catalog.ModelCatalog;
import com.networknt.genai.model.catalog.ModelDescription;
import com.networknt.genai.model.openai.OpenAiModelCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

import static com.networknt.genai.model.ModelProvider.OPEN_AI;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class OpenAiModelCatalogIT extends AbstractModelCatalogIT {

    @Override
    protected ModelCatalog createModelCatalog() {
        return OpenAiModelCatalog.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();
    }

    @Override
    protected ModelProvider expectedProvider() {
        return OPEN_AI;
    }

    @Test
    void should_have_owner_information() {
        ModelCatalog catalog = createModelCatalog();

        List<ModelDescription> models = catalog.listModels();

        assertThat(models).isNotEmpty();
        assertThat(models).anyMatch(m -> m.owner() != null);
    }

    @Test
    void should_have_creation_timestamp() {
        ModelCatalog catalog = createModelCatalog();

        List<ModelDescription> models = catalog.listModels();

        assertThat(models).isNotEmpty();
        assertThat(models).anyMatch(m -> m.createdAt() != null);
    }
}
