package com.fikafoodie.domain.ports;

import com.fikafoodie.domain.entities.Recipe;

public interface RecipeServicePort {
    Recipe getRecipeById(String id);
    Recipe addRecipe(Recipe recipe);
    Recipe updateRecipe(Recipe recipe);
    void deleteRecipeById(String id);
}
