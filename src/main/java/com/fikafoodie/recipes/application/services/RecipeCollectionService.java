package com.fikafoodie.recipes.application.services;

import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeGenerationServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;

import java.util.List;

/**
 * This class is responsible for managing the recipe collection of a user.
 * It interacts with several other services and repositories to generate recipes,
 * add them to the user's collection, and manage the user's credits.
 */
public class RecipeCollectionService {

    private final RecipeGenerationServicePort recipeGenerationServicePort;
    private final RecipeCollectionRepositoryPort recipeCollectionRepositoryPort;
    private final UserAccountSecuredServicePort userAccountSecuredServicePort;
    private final RecipeConfigurationPort recipeConfigurationPort;

    /**
     * Constructor for the RecipeCollectionService class.
     * @param recipeGenerationServicePort The service used to generate recipes.
     * @param recipeCollectionRepositoryPort The repository for storing and retrieving recipe collections.
     * @param userAccountSecuredServicePort The service for managing user accounts and credits.
     * @param recipeConfigurationPort The service for retrieving recipe configuration details.
     */
    public RecipeCollectionService(RecipeGenerationServicePort recipeGenerationServicePort, RecipeCollectionRepositoryPort recipeCollectionRepositoryPort, UserAccountSecuredServicePort userAccountSecuredServicePort, RecipeConfigurationPort recipeConfigurationPort) {
        this.recipeGenerationServicePort = recipeGenerationServicePort;
        this.recipeCollectionRepositoryPort = recipeCollectionRepositoryPort;
        this.userAccountSecuredServicePort = userAccountSecuredServicePort;
        this.recipeConfigurationPort = recipeConfigurationPort;
    }

    /**
     * Generates recipes based on the provided ingredients and adds them to the user's recipe collection.
     * The user's credits are checked before generating recipes and are deducted after successful generation.
     * @param ingredients The list of ingredients to generate recipes from.
     * @return The list of generated recipes.
     * @throws InsufficientCreditsException If the user does not have enough credits to generate recipes.
     */
    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficientCreditsException, UserAccountNotFoundException {
        UserAccount.Credits recipeCost = recipeConfigurationPort.getRecipeCreationCost();
        if (userAccountSecuredServicePort.getCreditBalance().compareTo(recipeCost) < 0){
            throw new InsufficientCreditsException("Insufficient credits to generate recipes");
        }
        List<Recipe> generatedRecipes = recipeGenerationServicePort.generateRecipesWithIngredients(ingredients);
        RecipeCollection recipeCollection = recipeCollectionRepositoryPort.getRecipeCollectionOfUser();
        generatedRecipes.forEach(recipeCollection::addRecipe);
        userAccountSecuredServicePort.subtractCredits(recipeCost);
        return generatedRecipes;
    }

    /**
     * Retrieves the user's current recipe collection.
     * @return The user's recipe collection.
     */
    public RecipeCollection getRecipeCollectionOfUser() {
        return recipeCollectionRepositoryPort.getRecipeCollectionOfUser();
    }

    public void addRecipeToCollection(Recipe recipe) {
        RecipeCollection recipeCollection = recipeCollectionRepositoryPort.getRecipeCollectionOfUser();
        recipeCollection.addRecipe(recipe);
    }
}