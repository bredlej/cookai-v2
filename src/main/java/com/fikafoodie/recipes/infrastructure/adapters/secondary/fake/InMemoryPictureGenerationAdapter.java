package com.fikafoodie.recipes.infrastructure.adapters.secondary.fake;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.recipes.domain.ports.secondary.PictureGenerationServicePort;
import com.fikafoodie.recipes.domain.valueobjects.Picture;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.fake.dto.PictureDTO;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;

@ApplicationScoped
@InMemory
public class InMemoryPictureGenerationAdapter implements PictureGenerationServicePort {
    @Override
    public Picture generatePicture(String prompt) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<PictureDTO> typeReference = new TypeReference<>() {
        };
        Picture picture = null;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("picture2_b64.json")) {
            PictureDTO parsedPicture = mapper.readValue(inputStream, typeReference);
            picture = new Picture(parsedPicture.picture());
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(InMemoryRecipeGenerationAdapter.class);
            log.warn("Unable to load recipes: " + e.getMessage());
        }
        return picture;
    }
}
