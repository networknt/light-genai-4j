package com.networknt.agent.model.image;

import com.networknt.agent.model.DisabledModelTest;
import org.junit.jupiter.api.Test;

class DisabledImageModelTest extends DisabledModelTest<ImageModel> {
    private ImageModel model = new DisabledImageModel();

    public DisabledImageModelTest() {
        super(ImageModel.class);
    }

    @Test
    void methodsShouldThrowException() {
        performAssertion(() -> this.model.generate("Hello"));
        performAssertion(() -> this.model.generate("Hello", 1));
        performAssertion(() -> this.model.edit(null, "Hello"));
        performAssertion(() -> this.model.edit(null, null, "Hello"));
    }
}