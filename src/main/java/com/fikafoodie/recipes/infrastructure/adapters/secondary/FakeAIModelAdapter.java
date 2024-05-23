package com.fikafoodie.recipes.infrastructure.adapters.secondary;

import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.AIServicePort;
import com.fikafoodie.recipes.domain.valueobjects.Ingredient;

import java.util.List;

public class FakeAIModelAdapter implements AIServicePort {
    @Override
    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) {
        Recipe recipe = new Recipe();
        recipe.setName(new Recipe.Name("Recipe1"));
        recipe.setIngredients(new Recipe.Ingredients(List.of(new Ingredient(), new Ingredient())));
        recipe.setId(new Recipe.Id("1"));
        recipe.setInstructions(new Recipe.Instructions(List.of("Instruction 1", "Instruction 2")));
        recipe.setTags(new Recipe.Tags(List.of("Tag 1", "Tag 2")));
        recipe.setNotes(new Recipe.Notes("Notes"));
        recipe.setPicture(new Recipe.Picture("Picture"));
        return List.of(recipe);
    }
}
