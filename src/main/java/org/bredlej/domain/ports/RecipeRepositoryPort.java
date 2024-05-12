package org.bredlej.domain.ports;

import org.bredlej.domain.entities.Recipe;

import java.util.Optional;

public interface RecipeRepositoryPort {
    Optional<Recipe> findById(String id);

    Recipe save(Recipe recipe);

}
