package com.fikafoodie.recipes.domain.ports.secondary;

import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.application.exceptions.RecipeCollectionNotFoundException;
import io.quarkus.security.Authenticated;

@Authenticated
public interface RecipeCollectionRepositoryPort {
    RecipeCollection getRecipeCollectionOfUser() throws RecipeCollectionNotFoundException;
    void addRecipeToCollection(Recipe recipe) throws RecipeCollectionNotFoundException;
    void updateRecipeInCollection(Recipe recipe) throws RecipeNotFoundException, RecipeCollectionNotFoundException;
}
