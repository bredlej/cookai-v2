package com.fikafoodie.recipes.application.services;

import com.fikafoodie.recipes.application.exceptions.InsufficentCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.entities.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.secondary.AIServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;
import com.fikafoodie.recipes.domain.ports.primary.RecipeCollectionServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountServicePort;

import java.util.List;


public class RecipeCollectionService implements RecipeCollectionServicePort {

    private final AIServicePort aiServicePort;
    private final RecipeCollectionRepositoryPort recipeCollectionRepositoryPort;
    private final UserAccountServicePort userAccountServicePort;
    private final RecipeConfigurationPort recipeConfigurationPort;

    public RecipeCollectionService(AIServicePort aiServicePort, RecipeCollectionRepositoryPort recipeCollectionRepositoryPort, UserAccountServicePort userAccountServicePort, RecipeConfigurationPort recipeConfigurationPort) {
        this.aiServicePort = aiServicePort;
        this.recipeCollectionRepositoryPort = recipeCollectionRepositoryPort;
        this.userAccountServicePort = userAccountServicePort;
        this.recipeConfigurationPort = recipeConfigurationPort;
    }


    @Override
    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficentCreditsException {
        UserAccount.Credits recipeCost = recipeConfigurationPort.getRecipeCreationCost();
        if (userAccountServicePort.getCreditBalance().compareTo(recipeCost) < 0){
            throw new InsufficentCreditsException("Insufficient credits to generate recipes");
        }
        List<Recipe> generatedRecipes = aiServicePort.generateRecipesWithIngredients(ingredients);
        RecipeCollection recipeCollection = recipeCollectionRepositoryPort.getRecipeCollectionOfUser();
        generatedRecipes.forEach(recipeCollection::addRecipe);
        userAccountServicePort.subtractCredits(recipeCost);
        return generatedRecipes;
    }

    @Override
    public RecipeCollection getRecipeCollectionOfUser() {
        return recipeCollectionRepositoryPort.getRecipeCollectionOfUser();
    }


}
