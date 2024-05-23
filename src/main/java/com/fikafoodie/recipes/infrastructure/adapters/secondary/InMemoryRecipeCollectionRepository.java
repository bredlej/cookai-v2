package com.fikafoodie.recipes.infrastructure.adapters.secondary;

import com.fikafoodie.recipes.domain.entities.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;

public class InMemoryRecipeCollectionRepository implements RecipeCollectionRepositoryPort {
    RecipeCollection recipeCollection = new RecipeCollection();
    @Override
    public RecipeCollection getRecipeCollectionOfUser() {
        return recipeCollection;
    }
}
