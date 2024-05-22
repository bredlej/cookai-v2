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
        recipeDTO.setId(recipe.getId().value());
        recipeDTO.setName(recipe.getName().value());
        recipeDTO.setIngredients(IngredientDTO.fromDomain(recipe.getIngredients().value()));
        recipeDTO.setInstructions(recipe.getInstructions().value());
        recipeDTO.setTags(recipe.getTags().value());
        recipeDTO.setPhoto(recipe.getPicture().value());
        recipeDTO.setNotes(recipe.getNotes().value());
        return recipeDTO;
    }
}
