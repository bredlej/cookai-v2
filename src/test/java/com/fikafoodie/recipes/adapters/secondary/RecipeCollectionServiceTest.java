package com.fikafoodie.recipes.adapters.secondary;

import com.fikafoodie.recipes.application.exceptions.InsufficentCreditsException;
import com.fikafoodie.recipes.application.services.RecipeCollectionService;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.entities.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.FakeAIModelAdapter;
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
    void generatedRecipesShouldBeAddedToCollection() throws InsufficentCreditsException {
        InMemoryUserAccountController userAccountController = new InMemoryUserAccountController(new UserAccount.Credits(1));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new FakeAIModelAdapter(),
                new InMemoryRecipeCollectionRepository(),
                userAccountController,
                recipeConfiguration);

        List<Recipe> generatedRecipes = recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));
        RecipeCollection recipeCollection = recipeCollectionService.getRecipeCollectionOfUser();

        generatedRecipes.forEach(recipe -> assertTrue(recipeCollection.getRecipes().value().contains(recipe)));
    }

    @Test
    void generatedRecipesShouldBePaidFor() throws InsufficentCreditsException {
        InMemoryUserAccountController userAccountController = new InMemoryUserAccountController(new UserAccount.Credits(1));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new FakeAIModelAdapter(),
                new InMemoryRecipeCollectionRepository(),
                userAccountController,
                recipeConfiguration);

        recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));

        Assertions.assertEquals(new UserAccount.Credits(0), userAccountController.getCreditBalance());
    }

    @Test()
    void shouldNotGenerateRecipesWithInsufficentCredits() {
        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new FakeAIModelAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountController(new UserAccount.Credits(0)),
                recipeConfiguration);

        try {
            recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));
        } catch (InsufficentCreditsException e) {
            assertTrue(true);
            return;
        }
        Assertions.fail();
    }
}
