package com.networknt.genai.model.image;

import com.networknt.genai.data.image.Image;
import com.networknt.genai.model.ModelDisabledException;
import com.networknt.genai.model.output.Response;

import java.util.List;

/**
 * An {@link ImageModel} which throws a {@link ModelDisabledException} for all of its methods
 * <p>
 *     This could be used in tests, or in libraries that extend this one to conditionally enable or disable functionality.
 * </p>
 */
public class DisabledImageModel implements ImageModel {

    /**
     * Creates a new instance.
     */
    public DisabledImageModel() {
    }
    @Override
    public Response<Image> generate(String prompt) {
        throw new ModelDisabledException("ImageModel is disabled");
    }

    @Override
    public Response<List<Image>> generate(String prompt, int n) {
        throw new ModelDisabledException("ImageModel is disabled");
    }

    @Override
    public Response<Image> edit(Image image, String prompt) {
        throw new ModelDisabledException("ImageModel is disabled");
    }

    @Override
    public Response<Image> edit(Image image, Image mask, String prompt) {
        throw new ModelDisabledException("ImageModel is disabled");
    }
}
