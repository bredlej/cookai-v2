package com.fikafoodie.recipes.domain.ports.secondary;

import com.fikafoodie.recipes.domain.entities.RecipeCollection;

public interface RecipeCollectionRepositoryPort {
    RecipeCollection getRecipeCollectionOfUser();
}
