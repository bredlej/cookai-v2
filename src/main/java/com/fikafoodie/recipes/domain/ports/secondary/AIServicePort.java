package com.fikafoodie.recipes.domain.ports.secondary;

import com.fikafoodie.recipes.domain.entities.Recipe;

import java.util.List;

public interface AIServicePort {
    List<Recipe> generateRecipesWithIngredients(List<String> ingredients);
}
