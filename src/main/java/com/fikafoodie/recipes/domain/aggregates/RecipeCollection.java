package com.fikafoodie.recipes.domain.aggregates;

import com.fikafoodie.recipes.domain.entities.Recipe;
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
