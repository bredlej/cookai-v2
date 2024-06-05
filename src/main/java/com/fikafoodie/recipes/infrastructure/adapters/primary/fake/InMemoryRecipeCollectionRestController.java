package com.fikafoodie.recipes.infrastructure.adapters.primary.fake;

import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.recipes.application.dto.RecipeCollectionDTO;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import com.fikafoodie.recipes.application.dto.RecipeGenerationIngredientsDTO;
import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.application.services.RecipeCollectionService;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.primary.RecipeCollectionServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeGenerationServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeNotFoundException;
import com.fikafoodie.recipes.application.exceptions.RecipeCollectionNotFoundException;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RequestScoped
@Path("/v1/collections")
public class InMemoryRecipeCollectionRestController implements RecipeCollectionServicePort {

    private final RecipeCollectionService recipeCollectionService;

    @Inject
    public InMemoryRecipeCollectionRestController(@InMemory RecipeGenerationServicePort recipeGenerationServicePort,
                                                  @InMemory RecipeCollectionRepositoryPort recipeCollectionRepositoryPort,
                                                  @InMemory UserAccountSecuredServicePort userAccountSecuredServicePort,
                                                  @InMemory RecipeConfigurationPort recipeConfigurationPort) {
        recipeCollectionService = new RecipeCollectionService(
                recipeGenerationServicePort,
                recipeCollectionRepositoryPort,
                userAccountSecuredServicePort,
                recipeConfigurationPort);
    }

    @GET
    @Path("/list")
    @Authenticated
    public Response getRecipeCollection() {
        RecipeCollectionDTO recipeCollectionDTO;
        try {
           recipeCollectionDTO = RecipeCollectionDTO.fromDomain(getRecipeCollectionOfUser());
        }
        catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
        return Response.ok().entity(recipeCollectionDTO).build();
    }

    @POST
    @Path("/add")
    public void addRecipe(RecipeDTO recipeDTO) {
        try {
            addRecipeToCollection(RecipeDTO.toDomain(recipeDTO));
        } catch (RecipeCollectionNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/generate")
    public Response generateRecipes(RecipeGenerationIngredientsDTO ingredients) {
        List<RecipeDTO> generatedRecipes;
        try {
            generatedRecipes = generateRecipesWithIngredients(ingredients.getIngredients()).stream().map(RecipeDTO::fromDomain).toList();
        }
        catch (InsufficientCreditsException | UserAccountNotFoundException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
        return Response.ok().entity(generatedRecipes).build();
    }

    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficientCreditsException, UserAccountNotFoundException {
        return recipeCollectionService.generateRecipesWithIngredients(ingredients);
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
}

