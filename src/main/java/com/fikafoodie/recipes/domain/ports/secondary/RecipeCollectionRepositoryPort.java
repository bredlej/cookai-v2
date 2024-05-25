package com.fikafoodie.recipes.domain.ports.secondary;

import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;

public interface RecipeCollectionRepositoryPort {
    RecipeCollection getRecipeCollectionOfUser();
}
