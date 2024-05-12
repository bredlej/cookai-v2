package org.bredlej.adapters.secondary;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.bredlej.domain.entities.Recipe;
import org.bredlej.domain.ports.RecipeRepositoryPort;
import org.bredlej.infrastructure.entities.RecipeEntity;

import java.util.Optional;

@ApplicationScoped
public class RecipeRepositoryAdapter implements RecipeRepositoryPort {
    public Optional<Recipe> findById(String id) {
        return RecipeEntity.findByIdOptional(id).map(entity -> ((RecipeEntity) entity).toDomain());
    }

    @Transactional
    public Recipe save(Recipe recipe) {
        RecipeEntity entity = RecipeEntity.fromDomain(recipe);
        entity.persist();
        return entity.toDomain();
    }
}
