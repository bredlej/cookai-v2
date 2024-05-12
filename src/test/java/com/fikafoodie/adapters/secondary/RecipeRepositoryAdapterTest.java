package com.fikafoodie.adapters.secondary;

import com.fikafoodie.domain.entities.Recipe;
import com.fikafoodie.domain.valueobjects.Ingredient;
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
        ingredient.setName("Ingredient 1");
        Recipe recipe = new Recipe();
        recipe.setId(UUID.randomUUID().toString());
        recipe.setName("Recipe 1");
        recipe.setIngredients(List.of(ingredient));
        recipe.setInstructions(List.of("Instruction 1", "Instruction 2"));
        recipe.setTags(List.of("Tag 1", "Tag 2"));
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
        ingredient.setName("Ingredient 1");
        Recipe recipe = new Recipe();
        recipe.setId(UUID.randomUUID().toString());
        recipe.setName("Recipe 1");
        recipe.setIngredients(List.of(ingredient));
        recipe.setInstructions(List.of("Instruction 1", "Instruction 2"));
        recipe.setTags(List.of("Tag 1", "Tag 2"));
        Recipe savedRecipe = recipeRepositoryAdapter.save(recipe);
        savedRecipe.setName("Recipe 2");
        savedRecipe.getIngredients().get(0).setName("Ingredient 2");

        // when
        Recipe updatedRecipe = recipeRepositoryAdapter.update(savedRecipe);

        // then
        assertThat(updatedRecipe.getId(), notNullValue());
        assertEquals(updatedRecipe.getName(), "Recipe 2");
        assertEquals(updatedRecipe.getIngredients().get(0).getName(), "Ingredient 2");

        Ingredient ingredientNew = new Ingredient();
        ingredientNew.setName("Ingredient 3");

        savedRecipe.setIngredients(List.of(ingredient, ingredientNew));
        Recipe updatedRecipeNew = recipeRepositoryAdapter.update(savedRecipe);
        assertEquals(updatedRecipeNew.getIngredients().size(), 2);
        assertEquals(updatedRecipeNew.getIngredients().get(0).getName(), "Ingredient 1");
        assertEquals(updatedRecipeNew.getIngredients().get(1).getName(), "Ingredient 3");

        updatedRecipeNew.setIngredients(List.of(ingredientNew));
        Recipe updatedRecipeNew2 = recipeRepositoryAdapter.update(updatedRecipeNew);
        assertEquals(updatedRecipeNew2.getIngredients().size(), 1);
        assertEquals(updatedRecipeNew2.getIngredients().get(0).getName(), "Ingredient 3");
    }

}