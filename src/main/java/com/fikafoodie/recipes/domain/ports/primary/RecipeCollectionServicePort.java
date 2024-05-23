package com.fikafoodie.recipes.domain.ports.primary;

import com.fikafoodie.recipes.application.exceptions.InsufficentCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.entities.RecipeCollection;

import java.util.List;

public interface RecipeCollectionServicePort {
    List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficentCreditsException;
    RecipeCollection getRecipeCollectionOfUser();
}
