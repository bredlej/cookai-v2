package com.fikafoodie.domain.ports;

import com.fikafoodie.domain.entities.Recipe;

import java.util.Optional;

public interface RecipeRepositoryPort {
    Optional<Recipe> findById(Recipe.Id id);

    Recipe save(Recipe recipe);

    void delete(Recipe recipe);

    Recipe update(Recipe recipe);
}
