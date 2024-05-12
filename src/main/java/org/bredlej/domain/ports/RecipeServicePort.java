package org.bredlej.domain.ports;

import org.bredlej.domain.entities.Recipe;

public interface RecipeServicePort {
    Recipe getRecipeById(String id);
    Recipe addRecipe(Recipe recipe);
}
