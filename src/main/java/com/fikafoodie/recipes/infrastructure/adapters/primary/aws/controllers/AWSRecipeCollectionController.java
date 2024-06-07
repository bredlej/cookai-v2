package com.fikafoodie.recipes.infrastructure.adapters.primary.aws.controllers;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.kernel.qualifiers.OpenAI;
import com.fikafoodie.recipes.application.dto.RecipeCollectionDTO;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import com.fikafoodie.recipes.application.dto.RecipeGenerationIngredientsDTO;
import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.application.services.RecipeCollectionService;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.primary.RecipeCollectionServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeGenerationServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeNotFoundException;
import com.fikafoodie.recipes.infrastructure.adapters.primary.aws.api.AWSRecipeCollectionControllerSecuredAPI;
import com.fikafoodie.recipes.application.exceptions.RecipeCollectionNotFoundException;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.List;

public class AWSRecipeCollectionController implements RecipeCollectionServicePort, AWSRecipeCollectionControllerSecuredAPI {

    private final RecipeCollectionService recipeCollectionService;

    public AWSRecipeCollectionController(
            @OpenAI RecipeGenerationServicePort recipeGenerationServicePort,
            @DynamoDB RecipeCollectionRepositoryPort recipeCollectionRepositoryPort,
            UserAccountSecuredServicePort userAccountSecuredServicePort,
            @InMemory RecipeConfigurationPort recipeConfigurationPort) {

        this.recipeCollectionService = new RecipeCollectionService(recipeGenerationServicePort, recipeCollectionRepositoryPort, userAccountSecuredServicePort, recipeConfigurationPort);
    }

    @Override
    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) {
        try {
            return recipeCollectionService.generateRecipesWithIngredients(ingredients);
        } catch (InsufficientCreditsException | UserAccountNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RecipeCollection getRecipeCollectionOfUser() {
        return recipeCollectionService.getRecipeCollectionOfUser();
    }

    @Override
    public void addRecipeToCollection(Recipe recipe) throws RecipeCollectionNotFoundException {
        recipeCollectionService.addRecipeToCollection(recipe);
    }

    @Override
    public void updateRecipeInCollection(Recipe recipe) throws RecipeNotFoundException, RecipeCollectionNotFoundException {
        recipeCollectionService.updateRecipeInCollection(recipe);
    }

    @Override
    public List<RecipeDTO> generateRecipes(RecipeGenerationIngredientsDTO ingredients) {
        return generateRecipesWithIngredients(ingredients.getIngredients()).stream()
                .map(RecipeDTO::fromDomain)
                .toList();
    }

    @Override
    public RecipeCollectionDTO getRecipes() {
        return RecipeCollectionDTO.fromDomain(getRecipeCollectionOfUser());
    }

    @Override
    public Response addRecipe(RecipeDTO recipeDTO) {
        try {
            addRecipeToCollection(RecipeDTO.toDomain(recipeDTO));
        } catch (RecipeCollectionNotFoundException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

    @Override
    public Response updateRecipe(RecipeDTO recipeDTO) {
        try {
            updateRecipeInCollection(RecipeDTO.toDomain(recipeDTO));
            return Response.ok().build();
        } catch (RecipeNotFoundException | RecipeCollectionNotFoundException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
    }
}
