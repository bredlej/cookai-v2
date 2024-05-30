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
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountServicePort;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.List;

@InMemory
@RequestScoped
@Path("/v1/collections")
public class InMemoryRecipeCollectionRestController implements RecipeCollectionServicePort {

    private final RecipeCollectionService recipeCollectionService;

    @Inject
    public InMemoryRecipeCollectionRestController(@InMemory RecipeGenerationServicePort recipeGenerationServicePort,
                                                 @InMemory RecipeCollectionRepositoryPort recipeCollectionRepositoryPort,
                                                 @InMemory UserAccountServicePort userAccountServicePort,
                                                 @InMemory RecipeConfigurationPort recipeConfigurationPort) {
        recipeCollectionService = new RecipeCollectionService(
                recipeGenerationServicePort,
                recipeCollectionRepositoryPort,
                userAccountServicePort,
                recipeConfigurationPort);
    }

    @GET
    @Path("/list")
    @Authenticated
    public RecipeCollectionDTO getRecipeCollection() {
        return RecipeCollectionDTO.fromDomain(getRecipeCollectionOfUser());
    }

    @POST
    @Path("/add")
    public void addRecipe(RecipeDTO recipeDTO) {
        addRecipeToCollection(RecipeDTO.toDomain(recipeDTO));
    }

    @POST
    @Path("/generate")
    public List<RecipeDTO> generateRecipes(RecipeGenerationIngredientsDTO ingredients) throws InsufficientCreditsException, UserAccountNotFoundException {
        return generateRecipesWithIngredients(ingredients.getIngredients()).stream().map(RecipeDTO::fromDomain).toList();
    }

    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficientCreditsException, UserAccountNotFoundException {
        return recipeCollectionService.generateRecipesWithIngredients(ingredients);
    }

    @Override
    public RecipeCollection getRecipeCollectionOfUser() {
        return recipeCollectionService.getRecipeCollectionOfUser();
    }

    @Override
    public void addRecipeToCollection(Recipe recipe) {
        recipeCollectionService.addRecipeToCollection(recipe);
    }
}
