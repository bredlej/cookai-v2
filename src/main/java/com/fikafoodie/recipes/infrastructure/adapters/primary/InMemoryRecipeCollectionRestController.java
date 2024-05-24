package com.fikafoodie.recipes.infrastructure.adapters.primary;

import com.fikafoodie.recipes.application.dto.RecipeCollectionDTO;
import com.fikafoodie.recipes.application.exceptions.InsufficentCreditsException;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.entities.RecipeCollection;
import com.fikafoodie.recipes.domain.ports.primary.RecipeCollectionServicePort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.InMemoryRecipeCollectionRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@RequestScoped
@Path("/collections")
public class InMemoryRecipeCollectionRestController implements RecipeCollectionServicePort {

    private final RecipeCollectionRepositoryPort recipeCollectionRepository;

    public InMemoryRecipeCollectionRestController() {
        this.recipeCollectionRepository = new InMemoryRecipeCollectionRepository();
    }

    @GET
    public RecipeCollectionDTO getRecipeCollection() {
        return RecipeCollectionDTO.fromDomain(getRecipeCollectionOfUser());
    }

    @Override
    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) throws InsufficentCreditsException {
        return List.of();
    }

    @Override
    public RecipeCollection getRecipeCollectionOfUser() {
        return recipeCollectionRepository.getRecipeCollectionOfUser();
    }
}
