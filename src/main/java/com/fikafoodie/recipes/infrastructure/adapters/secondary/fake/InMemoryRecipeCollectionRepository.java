package com.fikafoodie.recipes.infrastructure.adapters.secondary.fake;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.recipes.application.dto.RecipeCollectionDTO;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;

@ApplicationScoped
@InMemory
public class InMemoryRecipeCollectionRepository implements RecipeCollectionRepositoryPort {
    RecipeCollection recipeCollection = new RecipeCollection();

    public InMemoryRecipeCollectionRepository() {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<RecipeCollectionDTO> typeReference = new TypeReference<>(){};
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mocks/mockRecipeCollection.json");
        try {
            RecipeCollectionDTO recipeCollectionDTO = mapper.readValue(inputStream, typeReference);
            recipeCollectionDTO.getRecipes().forEach(recipeDTO -> recipeCollection.addRecipe(RecipeDTO.toDomain(recipeDTO)));
        } catch (IOException e){
            Logger log = LoggerFactory.getLogger(InMemoryRecipeCollectionRepository.class);
            log.warn("Unable to load recipes: " + e.getMessage());
        }
    }

    @Override
    public RecipeCollection getRecipeCollectionOfUser() {
        return recipeCollection;
    }

    @Override
    public void addRecipeToCollection(Recipe recipe) throws UserAccountNotFoundException {
        recipeCollection.addRecipe(recipe);
    }
}
