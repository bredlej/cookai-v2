package com.fikafoodie.recipes.infrastructure.adapters.primary;

import com.fikafoodie.recipes.application.dto.RecipeCollectionDTO;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import com.fikafoodie.recipes.application.dto.RecipeGenerationIngredientsDTO;
import com.fikafoodie.recipes.application.exceptions.InsufficientCreditsException;
import com.fikafoodie.recipes.application.services.RecipeCollectionService;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.primary.RecipeCollectionServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.FakeRecipeGenerationAdapter;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.InMemoryRecipeCollectionRepository;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.InMemoryUserAccountController;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.List;

@RequestScoped
@Path("/v1/collections")
public class InMemoryRecipeCollectionRestController implements RecipeCollectionServicePort {

    private final RecipeCollectionService recipeCollectionService;

    public InMemoryRecipeCollectionRestController() {
        recipeCollectionService = new RecipeCollectionService(
                new FakeRecipeGenerationAdapter(),
                new InMemoryRecipeCollectionRepository(),
                new InMemoryUserAccountController(new UserAccount.Credits(1)),
                () -> new UserAccount.Credits(1));
    }

    @GET
    @Path("/list")
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
    public List<RecipeDTO> generateRecipes(RecipeGenerationIngredientsDTO ingredients) throws InsufficientCreditsException {
        return generateRecipesWithIngredients(ingredients.getIngredients()).stream().map(RecipeDTO::fromDomain).toList();
    }

    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficientCreditsException {
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
