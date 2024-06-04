package com.fikafoodie.recipes.infrastructure.adapters.secondary.fake;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeGenerationServicePort;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import jakarta.enterprise.context.RequestScoped;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
@InMemory
public class InMemoryRecipeGenerationAdapter implements RecipeGenerationServicePort {
    List<Recipe> generatedRecipes = new ArrayList<>();

    public InMemoryRecipeGenerationAdapter() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<RecipeDTO>> typeReference = new TypeReference<>() {
        };
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mocks/mockGenerateRecipes.json")) {
            List<RecipeDTO> parsedRecipes = mapper.readValue(inputStream, typeReference);
            parsedRecipes.forEach(recipeDTO -> generatedRecipes.add(RecipeDTO.toDomain(recipeDTO)));
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(InMemoryRecipeGenerationAdapter.class);
            log.warn("Unable to load recipes: " + e.getMessage());
        }
    }

    @Override
    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) {
       return generatedRecipes;
    }
}
