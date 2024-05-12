package org.bredlej.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bredlej.domain.entities.Recipe;
import org.bredlej.domain.exceptions.EntityNotFoundException;
import org.bredlej.domain.ports.RecipeRepositoryPort;
import org.bredlej.domain.ports.RecipeServicePort;

@ApplicationScoped
public class ReportService implements RecipeServicePort {
    @Inject
    RecipeRepositoryPort recipeRepository;

    public Recipe getRecipeById(String id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe with ID " + id + " not found."));
    }

    @Override
    public Recipe addRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
}