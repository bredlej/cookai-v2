package com.fikafoodie.recipes.application.dto;

import com.fikafoodie.recipes.domain.entities.RecipeCollection;
import lombok.Data;

import java.util.List;

@Data
public class RecipeCollectionDTO {
    private List<RecipeDTO> recipes;

    public static RecipeCollectionDTO fromDomain(RecipeCollection recipeCollection) {
        RecipeCollectionDTO recipeCollectionDTO = new RecipeCollectionDTO();
        recipeCollectionDTO.setRecipes(recipeCollection.getRecipes().value().stream().map(RecipeDTO::fromDomain).toList());
        return recipeCollectionDTO;
    }
}
