package com.fikafoodie.recipes.domain.ports.primary;

import com.fikafoodie.recipes.domain.entities.Recipe;

public interface RecipeServicePort {
    Recipe getRecipeById(Recipe.Id id);
    Recipe addRecipe(Recipe recipe);
    Recipe updateRecipe(Recipe recipe);
    void deleteRecipeById(Recipe.Id id);
}
