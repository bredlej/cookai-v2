package com.fikafoodie.recipes.domain.ports.secondary;

import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import io.quarkus.security.Authenticated;

@Authenticated
public interface RecipeCollectionRepositoryPort {
    RecipeCollection getRecipeCollectionOfUser() throws UserAccountNotFoundException;
    void addRecipeToCollection(Recipe recipe) throws UserAccountNotFoundException;
}
