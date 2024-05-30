package com.fikafoodie.recipes.application.services;

import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.fake.InMemoryRecipeGenerationAdapter;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.fake.InMemoryRecipeCollectionRepository;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountPublicServicePort;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountPublicRepositoryPort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import com.fikafoodie.useraccount.domain.valueobjects.Password;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.open.InMemoryUserAccountPublicController;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.secured.InMemoryUserAccountSecuredController;
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
        InMemoryUserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        UserAccountPublicServicePort userAccountController = new InMemoryUserAccountPublicController(
                userAccountRepository,
                () -> new UserAccount.Credits(1)
        );
        userAccountController.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountController.confirmAccount(new UserAccount.Name("name"));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountSecuredController(userAccountRepository),
                recipeConfiguration);

        List<Recipe> generatedRecipes = recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));
        RecipeCollection recipeCollection = recipeCollectionService.getRecipeCollectionOfUser();

        generatedRecipes.forEach(recipe -> assertTrue(recipeCollection.getRecipes().value().contains(recipe)));
    }

    @Test
    void generatedRecipesShouldBePaidFor() throws InsufficientCreditsException, UserAccountNotFoundException {
        InMemoryUserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        UserAccountPublicServicePort userAccountController = new InMemoryUserAccountPublicController(
                userAccountRepository,
                () -> new UserAccount.Credits(1)
        );
        UserAccountSecuredServicePort userAccountSecuredController = new InMemoryUserAccountSecuredController(userAccountRepository);

        userAccountController.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountController.confirmAccount(new UserAccount.Name("name"));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountSecuredController(userAccountRepository),
                recipeConfiguration);

        recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2"));

        Assertions.assertEquals(new UserAccount.Credits(0), userAccountSecuredController.getCreditBalance());
    }

    @Test
    void shouldNotGenerateRecipesWithInsufficientCredits() throws UserAccountNotFoundException {
        UserAccountPublicRepositoryPort publicRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountPublicServicePort userAccountPublicServicePort = new InMemoryUserAccountPublicController(
                publicRepositoryPort,
                () -> new UserAccount.Credits(0)
        );
        userAccountPublicServicePort.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountPublicServicePort.confirmAccount(new UserAccount.Name("name"));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountSecuredController((UserAccountSecuredRepositoryPort) publicRepositoryPort),
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
