package com.fikafoodie.recipes.infrastructure.adapters.primary.aws.api;

import com.fikafoodie.recipes.application.dto.RecipeCollectionDTO;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import com.fikafoodie.recipes.application.dto.RecipeGenerationIngredientsDTO;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/v1/recipes")
public interface AWSRecipeCollectionControllerSecuredAPI {

    @Authenticated
    @POST
    @Path("/generate")
    List<RecipeDTO> generateRecipes(RecipeGenerationIngredientsDTO ingredients);

    @Authenticated
    @GET
    @Path("/list")
    RecipeCollectionDTO getRecipes();

    @Authenticated
    @POST
    @Path("/add")
    Response addRecipe(RecipeDTO recipeDTO);

    @Authenticated
    @POST
    @Path("/edit")
    Response updateRecipe(RecipeDTO recipeDTO);
}
