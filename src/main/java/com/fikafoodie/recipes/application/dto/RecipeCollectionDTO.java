package com.fikafoodie.recipes.application.dto;

import com.fikafoodie.recipes.domain.entities.RecipeCollection;
import lombok.Data;

import java.util.List;

@Data
public class RecipeCollectionDTO {
    private String id;
    private List<RecipeDTO> recipes;

    public static RecipeCollectionDTO fromDomain(RecipeCollection recipeCollection) {
        RecipeCollectionDTO recipeCollectionDTO = new RecipeCollectionDTO();
        recipeCollectionDTO.setId(recipeCollection.getId().value());
        recipeCollectionDTO.setRecipes(recipeCollection.getRecipes().value().stream().map(RecipeDTO::fromDomain).toList());
        return recipeCollectionDTO;
    }
}
