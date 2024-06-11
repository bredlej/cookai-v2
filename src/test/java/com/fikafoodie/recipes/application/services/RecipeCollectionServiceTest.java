package com.fikafoodie.recipes.application.services;

import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeNotFoundException;
import com.fikafoodie.recipes.domain.valueobjects.Ingredient;
import com.fikafoodie.recipes.application.exceptions.RecipeCollectionNotFoundException;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.fake.InMemoryRecipeGenerationAdapter;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.fake.InMemoryRecipeCollectionRepository;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountPublicServicePort;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.domain.valueobjects.Password;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.open.InMemoryUserAccountPublicController;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.secured.InMemoryUserAccountSecuredController;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.fake.InMemoryUserAccountRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.jetbrains.annotations.NotNull;
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
        InMemoryUserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        UserAccountPublicServicePort userAccountPublicServicePort = new InMemoryUserAccountPublicController(
                userAccountRepository,
                () -> new UserAccount.Credits(0)
        );
        userAccountPublicServicePort.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountPublicServicePort.confirmAccount(new UserAccount.Name("name"));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountSecuredController(userAccountRepository),
                recipeConfiguration);

        Assertions.assertThrows(InsufficientCreditsException.class, () -> recipeCollectionService.generateRecipesWithIngredients(List.of("ingredient1", "ingredient2")));
    }

    @Test
    void shouldAddNewRecipeToCollection() throws UserAccountNotFoundException, RecipeCollectionNotFoundException {
        InMemoryUserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        UserAccountPublicServicePort userAccountPublicServicePort = new InMemoryUserAccountPublicController(
                userAccountRepository,
                () -> new UserAccount.Credits(0)
        );
        userAccountPublicServicePort.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountPublicServicePort.confirmAccount(new UserAccount.Name("name"));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountSecuredController(userAccountRepository),
                recipeConfiguration);

        Recipe recipe = recipeWithId("id");
        recipeCollectionService.addRecipeToCollection(recipe);

        RecipeCollection recipeCollection = recipeCollectionService.getRecipeCollectionOfUser();
        assertTrue(recipeCollection.getRecipes().value().contains(recipe));
    }

    @Test
    void shouldNotSubtractCreditsWhenNewRecipeIsAdded() throws UserAccountNotFoundException, RecipeCollectionNotFoundException {
        InMemoryUserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        UserAccountPublicServicePort userAccountPublicServicePort = new InMemoryUserAccountPublicController(
                userAccountRepository,
                () -> new UserAccount.Credits(0)
        );
        userAccountPublicServicePort.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountPublicServicePort.confirmAccount(new UserAccount.Name("name"));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountSecuredController(userAccountRepository),
                recipeConfiguration);

        Recipe recipe = recipeWithId("id");
        recipeCollectionService.addRecipeToCollection(recipe);

        Assertions.assertEquals(new UserAccount.Credits(0), userAccountRepository.getCreditBalance());
    }

    @Test
    void shouldUpdateRecipeIfInCollection() throws RecipeNotFoundException, UserAccountNotFoundException, RecipeCollectionNotFoundException {
        InMemoryUserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        UserAccountPublicServicePort userAccountPublicServicePort = new InMemoryUserAccountPublicController(
                userAccountRepository,
                () -> new UserAccount.Credits(0)
        );
        userAccountPublicServicePort.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountPublicServicePort.confirmAccount(new UserAccount.Name("name"));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountSecuredController(userAccountRepository),
                recipeConfiguration);

        Recipe recipe = recipeWithId("id");
        recipeCollectionService.addRecipeToCollection(recipe);

        recipe.setName(new Recipe.Name("new name"));
        recipeCollectionService.updateRecipeInCollection(recipe);

        RecipeCollection recipeCollection = recipeCollectionService.getRecipeCollectionOfUser();
        recipeCollection.getRecipes().value().stream().filter(r -> r.getId().equals(recipe.getId())).findFirst().ifPresent(r -> Assertions.assertEquals("new name", r.getName().value()));
    }

    @Test
    void shouldNotUpdateRecipeIfNotInCollection() throws UserAccountNotFoundException {
        InMemoryUserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        UserAccountPublicServicePort userAccountPublicServicePort = new InMemoryUserAccountPublicController(
                userAccountRepository,
                () -> new UserAccount.Credits(0)
        );
        userAccountPublicServicePort.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountPublicServicePort.confirmAccount(new UserAccount.Name("name"));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountSecuredController(userAccountRepository),
                recipeConfiguration);

        Recipe recipe = recipeWithId("not existing id");

        Assertions.assertThrows(RecipeNotFoundException.class, () -> recipeCollectionService.updateRecipeInCollection(recipe));
    }

    @Test
    void shouldNotSubtractCreditsWhenRecipeIsUpdated() throws RecipeNotFoundException, UserAccountNotFoundException, RecipeCollectionNotFoundException {
        InMemoryUserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
        UserAccountPublicServicePort userAccountPublicServicePort = new InMemoryUserAccountPublicController(
                userAccountRepository,
                () -> new UserAccount.Credits(0)
        );
        userAccountPublicServicePort.registerAccount(new UserAccount.Name("name"), new UserAccount.Email("email"), new Password("password"));
        userAccountPublicServicePort.confirmAccount(new UserAccount.Name("name"));

        RecipeCollectionService recipeCollectionService = new RecipeCollectionService(
                new InMemoryRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountSecuredController(userAccountRepository),
                recipeConfiguration);

        Recipe recipe = recipeWithId("id");
        recipeCollectionService.addRecipeToCollection(recipe);

        recipe.setName(new Recipe.Name("new name"));
        recipeCollectionService.updateRecipeInCollection(recipe);

        Assertions.assertEquals(new UserAccount.Credits(0), userAccountRepository.getCreditBalance());
    }

    private static @NotNull Recipe recipeWithId(String id) {
        Recipe recipe = new Recipe();
        recipe.setId(new Recipe.Id(id));
        recipe.setName(new Recipe.Name("name"));
        recipe.setSummary(new Recipe.Summary("summary"));
        recipe.setIngredients(new Recipe.Ingredients(List.of(new Ingredient(new Ingredient.Name("ingredient"), new Ingredient.Quantity(1), new Ingredient.Unit("unit"), false, new Ingredient.Type("type")))));
        recipe.setInstructions(new Recipe.Instructions(List.of("instruction")));
        recipe.setNotes(new Recipe.Notes("notes"));
        recipe.setPicture(new Recipe.Picture("picture"));
        recipe.setTags(new Recipe.Tags(List.of("tag")));
        return recipe;
    }
}
