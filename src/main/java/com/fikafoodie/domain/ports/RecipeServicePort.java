package com.fikafoodie.domain.ports;

import com.fikafoodie.domain.entities.Recipe;

public interface RecipeServicePort {
    Recipe getRecipeById(Recipe.Id id);
    Recipe addRecipe(Recipe recipe);
    Recipe updateRecipe(Recipe recipe);
    void deleteRecipeById(Recipe.Id id);
}
