package com.fikafoodie.recipes.infrastructure.adapters.primary.aws.controllers;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.kernel.qualifiers.OpenAI;
import com.fikafoodie.kernel.qualifiers.S3;
import com.fikafoodie.recipes.application.dto.RecipeCollectionDTO;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import com.fikafoodie.recipes.application.dto.RecipeGenerationIngredientsDTO;
import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.application.services.RecipeCollectionService;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.primary.RecipeCollectionServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.*;
import com.fikafoodie.recipes.infrastructure.adapters.primary.aws.api.AWSRecipeCollectionControllerSecuredAPI;
import com.fikafoodie.recipes.application.exceptions.RecipeCollectionNotFoundException;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.api.UserAccountControllerPublicAPI;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@ApplicationScoped
public class AWSRecipeCollectionController implements RecipeCollectionServicePort, AWSRecipeCollectionControllerSecuredAPI {

    private final RecipeCollectionService recipeCollectionService;

    private final JsonWebToken jwt;

    @Inject
    public AWSRecipeCollectionController(
            @OpenAI RecipeGenerationServicePort recipeGenerationServicePort,
            @DynamoDB RecipeCollectionRepositoryPort recipeCollectionRepositoryPort,
            UserAccountSecuredServicePort userAccountSecuredServicePort,
            @InMemory RecipeConfigurationPort recipeConfigurationPort,
            @OpenAI PictureGenerationServicePort pictureGenerationServicePort,
            @S3 PictureStorageServicePort pictureStorageServicePort,
            JsonWebToken jwt
    ) {
        this.jwt = jwt;
        this.recipeCollectionService = new RecipeCollectionService(
                recipeGenerationServicePort,
                recipeCollectionRepositoryPort,
                userAccountSecuredServicePort,
                recipeConfigurationPort,
                pictureGenerationServicePort,
                pictureStorageServicePort,
                new UserAccount.Name(jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM)));
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
