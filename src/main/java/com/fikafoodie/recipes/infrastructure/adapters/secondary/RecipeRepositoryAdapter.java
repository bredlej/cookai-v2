package com.fikafoodie.recipes.infrastructure.adapters.secondary;

import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeRepositoryPort;
import com.fikafoodie.recipes.infrastructure.entities.RecipeEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class RecipeRepositoryAdapter implements RecipeRepositoryPort {
    public Optional<Recipe> findById(Recipe.Id id) {
        return RecipeEntity.findByIdOptional(id.value()).map(entity -> ((RecipeEntity) entity).toDomain());
    }

    @Transactional
    public Recipe save(Recipe recipe) {
        RecipeEntity entity = RecipeEntity.fromDomain(recipe);
        entity.getIngredients().forEach(ingredientEntity -> ingredientEntity.persist());
        entity.persist();
        return entity.toDomain();
    }

    @Override
    public void delete(Recipe recipe) {
        RecipeEntity.deleteById(recipe.getId().value());
    }

    @Transactional
    public Recipe update(Recipe recipe) {
        RecipeEntity dbEntity = RecipeEntity.findById(recipe.getId().value());
        return dbEntity.update(recipe).toDomain();
    }

}
