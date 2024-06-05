package com.fikafoodie.recipes.domain.aggregates;

import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeNotFoundException;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeCollection {
    private Id id = new Id("0");
    private UserAccount.Id ownerId = new UserAccount.Id("0");
    private Recipes recipes = new Recipes(new ArrayList<>());

    public void addRecipe(Recipe recipe) {
        recipes.value().add(recipe);
    }

    public void updateRecipe(Recipe recipe) throws RecipeNotFoundException {
        if (recipes.value().stream().noneMatch(r -> r.getId().equals(recipe.getId()))) {
            throw new RecipeNotFoundException("Recipe not found in collection");
        }
        recipes.value().stream()
                .filter(r -> r.getId().equals(recipe.getId()))
                .findFirst()
                .ifPresent(r -> {
                    recipes.value().set(recipes.value().indexOf(r), recipe);
                });
    }

    public record Id(String value) {
        public Id {
            if (value == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
        }
    }

    public record Recipes(List<Recipe> value) {
        public Recipes {
            if (value == null) {
                throw new IllegalArgumentException("Recipes cannot be null");
            }
        }
    }

}
