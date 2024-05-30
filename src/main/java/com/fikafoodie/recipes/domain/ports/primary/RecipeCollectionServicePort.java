package com.fikafoodie.recipes.domain.ports.primary;

import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;

import java.util.List;

public interface RecipeCollectionServicePort {
    List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficientCreditsException, UserAccountNotFoundException;
    RecipeCollection getRecipeCollectionOfUser();
    void addRecipeToCollection(Recipe recipe);
}
