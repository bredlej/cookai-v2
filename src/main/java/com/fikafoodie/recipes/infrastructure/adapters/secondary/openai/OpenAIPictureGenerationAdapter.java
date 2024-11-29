package com.fikafoodie.recipes.infrastructure.adapters.secondary.openai;

import com.fikafoodie.kernel.qualifiers.OpenAI;
import com.fikafoodie.recipes.domain.ports.secondary.PictureGenerationServicePort;
import com.fikafoodie.recipes.domain.valueobjects.Picture;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


@ApplicationScoped
@OpenAI
public class OpenAIPictureGenerationAdapter implements PictureGenerationServicePort {

    private final ImageModel imageModel;

    public OpenAIPictureGenerationAdapter(@ConfigProperty(name = "quarkus.langchain4j.openai.api-key") String apiKey) {
        imageModel = OpenAiImageModel.withApiKey(apiKey);
    }

    @Override
    public Picture generatePicture(String prompt) {
        Response<Image> response = imageModel.generate(prompt);
        try (InputStream in = response.content().url().toURL().openStream()) {
            return new Picture(
                    Base64.getEncoder().encodeToString(in.readAllBytes())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
