package com.fikafoodie.recipes.adapters.secondary;

import com.fikafoodie.recipes.infrastructure.adapters.secondary.RecipeRepositoryAdapter;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.valueobjects.Ingredient;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
class RecipeRepositoryAdapterTest {

    @Inject
    RecipeRepositoryAdapter recipeRepositoryAdapter;

    @Test
    @Transactional
    void shouldCreateEntityAndFindItDeleteItAndNotFindIt() {
        // given
        Ingredient ingredient = new Ingredient();
        ingredient.setName(new Ingredient.Name("Ingredient 1"));
        Recipe recipe = new Recipe();
        recipe.setId(new Recipe.Id(UUID.randomUUID().toString()));
        recipe.setName(new Recipe.Name("Recipe 1"));
        recipe.setIngredients(new Recipe.Ingredients(List.of(ingredient)));
        recipe.setInstructions(new Recipe.Instructions(List.of("Instruction 1", "Instruction 2")));
        recipe.setTags(new Recipe.Tags(List.of("Tag 1", "Tag 2")));
        // when
        Recipe savedRecipe = recipeRepositoryAdapter.save(recipe);

        // then
        assertThat(savedRecipe.getId(), notNullValue());

        recipeRepositoryAdapter.delete(savedRecipe);

        assertTrue(recipeRepositoryAdapter.findById(savedRecipe.getId()).isEmpty());
    }

    @Test
    @Transactional
    void shouldUpdateEntity() {
        // given
        Ingredient ingredient = new Ingredient();
        ingredient.setName(new Ingredient.Name("Ingredient 1"));
        Recipe recipe = new Recipe();
        recipe.setId(new Recipe.Id(UUID.randomUUID().toString()));
        recipe.setName(new Recipe.Name("Recipe 1"));
        recipe.setIngredients(new Recipe.Ingredients(List.of(ingredient)));
        recipe.setInstructions(new Recipe.Instructions(List.of("Instruction 1", "Instruction 2")));
        recipe.setTags(new Recipe.Tags(List.of("Tag 1", "Tag 2")));
        Recipe savedRecipe = recipeRepositoryAdapter.save(recipe);
        savedRecipe.setName(new Recipe.Name("Recipe 2"));

        savedRecipe.getIngredients().value().getFirst().setName(new Ingredient.Name("Ingredient 2"));

        // when
        Recipe updatedRecipe = recipeRepositoryAdapter.update(savedRecipe);

        // then
        assertThat(updatedRecipe.getId().value(), notNullValue());
        assertEquals(updatedRecipe.getName().value(), "Recipe 2");
        assertEquals(updatedRecipe.getIngredients().value().getFirst().getName().value(), "Ingredient 2");

        Ingredient ingredientNew = new Ingredient();
        ingredientNew.setName(new Ingredient.Name("Ingredient 3"));

        savedRecipe.setIngredients(new Recipe.Ingredients(List.of(ingredient, ingredientNew)));
        Recipe updatedRecipeNew = recipeRepositoryAdapter.update(savedRecipe);
        assertEquals(updatedRecipeNew.getIngredients().value().size(), 2);
        assertEquals(updatedRecipeNew.getIngredients().value().get(0).getName().value(), "Ingredient 1");
        assertEquals(updatedRecipeNew.getIngredients().value().get(1).getName().value(), "Ingredient 3");

        updatedRecipeNew.setIngredients(new Recipe.Ingredients(List.of(ingredientNew)));
        Recipe updatedRecipeNew2 = recipeRepositoryAdapter.update(updatedRecipeNew);
        assertEquals(updatedRecipeNew2.getIngredients().value().size(), 1);
        assertEquals(updatedRecipeNew2.getIngredients().value().getFirst().getName().value(), "Ingredient 3");
    }

}