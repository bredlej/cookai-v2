package com.fikafoodie.recipes.adapters.secondary;

import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.application.services.RecipeCollectionService;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.FakeRecipeGenerationAdapter;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.InMemoryRecipeCollectionRepository;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.InMemoryUserAccountController;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
public class RecipeCollectionServiceTest {

    RecipeConfigurationPort recipeConfiguration = () -> new UserAccount.Credits(1);

    @Test
    void generatedRecipesShouldBeAddedToCollection() throws InsufficientCreditsException {
        InMemoryUserAccountController userAccountController = new InMemoryUserAccountController(new UserAccount.Credits(1));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new FakeRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                userAccountController,
                recipeConfiguration);

        List<Recipe> generatedRecipes = recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));
        RecipeCollection recipeCollection = recipeCollectionService.getRecipeCollectionOfUser();

        generatedRecipes.forEach(recipe -> assertTrue(recipeCollection.getRecipes().value().contains(recipe)));
    }

    @Test
    void generatedRecipesShouldBePaidFor() throws InsufficientCreditsException {
        InMemoryUserAccountController userAccountController = new InMemoryUserAccountController(new UserAccount.Credits(1));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new FakeRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                userAccountController,
                recipeConfiguration);

        recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));

        Assertions.assertEquals(new UserAccount.Credits(0), userAccountController.getCreditBalance());
    }

    @Test
    void shouldNotGenerateRecipesWithInsufficientCredits() {
        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new FakeRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountController(new UserAccount.Credits(0)),
                recipeConfiguration);

        var amountOfRecipesBefore = recipeCollectionService.getRecipeCollectionOfUser().getRecipes().value().size();
        try {
            recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));
        } catch (InsufficientCreditsException e) {
            var amountOfRecipesAfter = recipeCollectionService.getRecipeCollectionOfUser().getRecipes().value().size();
            Assertions.assertEquals(amountOfRecipesBefore, amountOfRecipesAfter);
            return;
        }
        Assertions.fail();
    }
}
