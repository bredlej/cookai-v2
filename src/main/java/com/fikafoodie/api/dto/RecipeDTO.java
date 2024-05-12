package com.fikafoodie.api.dto;

import com.fikafoodie.domain.entities.Recipe;
import lombok.Data;

import java.util.List;

@Data
public class RecipeDTO {
    private String id;
    private String name;
    private List<IngredientDTO> ingredients;
    private List<String> instructions;
    private List<String> tags;
    private String photo;
    private String notes;

    public static RecipeDTO fromDomain(Recipe recipe) {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(recipe.getId());
        recipeDTO.setName(recipe.getName());
        recipeDTO.setIngredients(IngredientDTO.fromDomain(recipe.getIngredients()));
        recipeDTO.setInstructions(recipe.getInstructions());
        recipeDTO.setTags(recipe.getTags());
        recipeDTO.setPhoto(recipe.getPhoto());
        recipeDTO.setNotes(recipe.getNotes());
        return recipeDTO;
    }
}
