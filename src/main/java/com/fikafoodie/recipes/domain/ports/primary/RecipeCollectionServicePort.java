package com.fikafoodie.recipes.domain.ports.primary;

import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;

import java.util.List;

public interface RecipeCollectionServicePort {
    List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficientCreditsException;
    RecipeCollection getRecipeCollectionOfUser();
    void addRecipeToCollection(Recipe recipe);
}
