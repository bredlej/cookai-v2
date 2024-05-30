package com.fikafoodie.recipes.application.services;

import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.fake.InMemoryRecipeGenerationAdapter;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.fake.InMemoryRecipeCollectionRepository;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountServicePort;
import com.fikafoodie.useraccount.domain.valueobjects.Password;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.InMemoryUserAccountController;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.fake.InMemoryUserAccountRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
public class RecipeCollectionServiceTest {

    RecipeConfigurationPort recipeConfiguration = () -> new UserAccount.Credits(1);

    @Test
    void generatedRecipesShouldBeAddedToCollection() throws InsufficientCreditsException, UserAccountNotFoundException {
        InMemoryUserAccountController userAccountController = new InMemoryUserAccountController(
                new InMemoryUserAccountRepository(),
                () -> new UserAccount.Credits(1)
        );
        userAccountController.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountController.confirmAccount();

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                userAccountController,
                recipeConfiguration);

        List<Recipe> generatedRecipes = recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));
        RecipeCollection recipeCollection = recipeCollectionService.getRecipeCollectionOfUser();

        generatedRecipes.forEach(recipe -> assertTrue(recipeCollection.getRecipes().value().contains(recipe)));
    }

    @Test
    void generatedRecipesShouldBePaidFor() throws InsufficientCreditsException, UserAccountNotFoundException {
        UserAccountServicePort userAccountController = new InMemoryUserAccountController(
                new InMemoryUserAccountRepository(),
                () -> new UserAccount.Credits(1)
        );
        userAccountController.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountController.confirmAccount();

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                userAccountController,
                recipeConfiguration);

        recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));

        Assertions.assertEquals(new UserAccount.Credits(0), userAccountController.getCreditBalance());
    }

    @Test
    void shouldNotGenerateRecipesWithInsufficientCredits() throws UserAccountNotFoundException {
        UserAccountServicePort userAccountServicePort = new InMemoryUserAccountController(
                new InMemoryUserAccountRepository(),
                () -> new UserAccount.Credits(0)
        );
        userAccountServicePort.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountServicePort.confirmAccount();

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                userAccountServicePort,
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
