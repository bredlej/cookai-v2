package com.fikafoodie.domain.services;

import com.fikafoodie.domain.entities.Recipe;
import com.fikafoodie.domain.exceptions.EntityNotFoundException;
import com.fikafoodie.domain.ports.RecipeRepositoryPort;
import com.fikafoodie.domain.ports.RecipeServicePort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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
        return recipeRepository.findById(recipe.getId()).orElseGet(() -> recipeRepository.save(recipe));
    }

    @Override
    public Recipe updateRecipe(Recipe recipe) {
        checkIfRecipeExists(recipe.getId());
        return recipeRepository.update(recipe);
    }

    @Override
    public void deleteRecipeById(String id) {
        Recipe recipe = getRecipeById(id);
        recipeRepository.delete(recipe);
    }

    private void checkIfRecipeExists(String id) {
        recipeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Recipe with ID " + id + " not found."));
    }
}