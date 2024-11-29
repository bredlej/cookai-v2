package com.fikafoodie.recipes.application.services;

import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.secondary.*;
import com.fikafoodie.recipes.application.exceptions.RecipeCollectionNotFoundException;
import com.fikafoodie.recipes.domain.valueobjects.Picture;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class is responsible for managing the recipe collection of a user.
 * It interacts with several other services and repositories to generate recipes,
 * add them to the user's collection, and manage the user's credits.
 */
public class RecipeCollectionService {

    private final Logger logger = Logger.getLogger(RecipeCollectionService.class.getName());

    private final RecipeGenerationServicePort recipeGenerationServicePort;
    private final RecipeCollectionRepositoryPort recipeCollectionRepositoryPort;
    private final UserAccountSecuredServicePort userAccountSecuredServicePort;
    private final RecipeConfigurationPort recipeConfigurationPort;
    private final PictureGenerationServicePort pictureGenerationServicePort;
    private final PictureStorageServicePort pictureStorageServicePort;
    private final UserAccount.Name userName;

    /**
     * Constructor for the RecipeCollectionService class.
     *
     * @param recipeGenerationServicePort    The service used to generate recipes.
     * @param recipeCollectionRepositoryPort The repository for storing and retrieving recipe collections.
     * @param userAccountSecuredServicePort  The service for managing user accounts and credits.
     * @param recipeConfigurationPort        The service for retrieving recipe configuration details.
     */
    public RecipeCollectionService(RecipeGenerationServicePort recipeGenerationServicePort,
                                   RecipeCollectionRepositoryPort recipeCollectionRepositoryPort,
                                   UserAccountSecuredServicePort userAccountSecuredServicePort,
                                   RecipeConfigurationPort recipeConfigurationPort,
                                   PictureGenerationServicePort pictureGenerationServicePort,
                                   PictureStorageServicePort pictureStorageServicePort,
                                   UserAccount.Name userName) {
        this.recipeGenerationServicePort = recipeGenerationServicePort;
        this.recipeCollectionRepositoryPort = recipeCollectionRepositoryPort;
        this.userAccountSecuredServicePort = userAccountSecuredServicePort;
        this.recipeConfigurationPort = recipeConfigurationPort;
        this.pictureGenerationServicePort = pictureGenerationServicePort;
        this.pictureStorageServicePort = pictureStorageServicePort;
        this.userName = userName;
    }

    /**
     * Generates recipes based on the provided ingredients and adds them to the user's recipe collection.
     * The user's credits are checked before generating recipes and are deducted after successful generation.
     *
     * @param ingredients The list of ingredients to generate recipes from.
     * @return The list of generated recipes.
     * @throws InsufficientCreditsException If the user does not have enough credits to generate recipes.
     * @throws UserAccountNotFoundException If the user account is not found.
     */
    @Transactional
    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficientCreditsException, UserAccountNotFoundException {
        UserAccount.Credits recipeCost = recipeConfigurationPort.getRecipeCreationCost();
        if (userAccountSecuredServicePort.getCreditBalance().compareTo(recipeCost) < 0) {
            throw new InsufficientCreditsException("Insufficient credits to generate recipes");
        }

        List<Recipe> generatedRecipes = recipeGenerationServicePort.generateRecipesWithIngredients(ingredients);
        generatedRecipes.forEach(recipe -> {
            try {
                recipe.setId(new Recipe.Id(UUID.randomUUID().toString()));
                Picture picture = pictureGenerationServicePort.generatePicture(recipe.getPrompt().value());
                recipe.setPicture(pictureStorageServicePort.storePicture(picture, userName, recipe.getId()));

                recipeCollectionRepositoryPort.addRecipeToCollection(recipe);
                userAccountSecuredServicePort.subtractCredits(recipeCost);
            } catch (RecipeCollectionNotFoundException | UserAccountNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        return generatedRecipes;
    }

    /**
     * Retrieves the user's current recipe collection.
     *
     * @return The user's recipe collection.
     */
    public RecipeCollection getRecipeCollectionOfUser() {
        try {
            return recipeCollectionRepositoryPort.getRecipeCollectionOfUser();
        } catch (RecipeCollectionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addRecipeToCollection(Recipe recipe) throws RecipeCollectionNotFoundException {
        recipeCollectionRepositoryPort.addRecipeToCollection(recipe);
    }

    public void updateRecipeInCollection(Recipe recipe) throws RecipeNotFoundException, RecipeCollectionNotFoundException {
        recipeCollectionRepositoryPort.updateRecipeInCollection(recipe);
    }
}