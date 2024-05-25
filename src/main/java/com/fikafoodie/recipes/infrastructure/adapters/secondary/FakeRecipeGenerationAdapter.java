package com.fikafoodie.recipes.infrastructure.adapters.secondary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeGenerationServicePort;
import com.fikafoodie.recipes.domain.valueobjects.Ingredient;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FakeRecipeGenerationAdapter implements RecipeGenerationServicePort {
    List<Recipe> generatedRecipes = new ArrayList<>();

    public FakeRecipeGenerationAdapter() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<RecipeDTO>> typeReference = new TypeReference<>() {
        };
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mocks/mockGenerateRecipes.json")) {
            List<RecipeDTO> parsedRecipes = mapper.readValue(inputStream, typeReference);
            parsedRecipes.forEach(recipeDTO -> generatedRecipes.add(RecipeDTO.toDomain(recipeDTO)));
        } catch (Exception e) {
            Logger log = LoggerFactory.getLogger(FakeRecipeGenerationAdapter.class);
            log.warn("Unable to load recipes: " + e.getMessage());
        }
    }

    @Override
    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) {
       return generatedRecipes;
    }
}
