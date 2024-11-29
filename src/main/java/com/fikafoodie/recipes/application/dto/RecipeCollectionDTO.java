package com.fikafoodie.recipes.application.dto;

import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.util.List;

@RegisterForReflection
@Data
public class RecipeCollectionDTO {
    private List<RecipeDTO> recipes;

    public static RecipeCollectionDTO fromDomain(RecipeCollection recipeCollection) {
        RecipeCollectionDTO recipeCollectionDTO = new RecipeCollectionDTO();
        recipeCollectionDTO.setRecipes(recipeCollection.getRecipes().value().stream().map(RecipeDTO::fromDomain).toList());
        return recipeCollectionDTO;
    }
}
