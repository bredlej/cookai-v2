package org.bredlej.api.dto;

import lombok.Data;
import org.bredlej.domain.entities.Tag;

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

    public static RecipeDTO fromDomain(org.bredlej.domain.entities.Recipe recipe) {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(recipe.getId());
        recipeDTO.setName(recipe.getName());
        recipeDTO.setIngredients(IngredientDTO.fromDomain(recipe.getIngredients()));
        recipeDTO.setInstructions(recipe.getInstructions());
        recipeDTO.setTags(recipe.getTags().stream().map(Tag::getName).toList());
        recipeDTO.setPhoto(recipe.getPhoto());
        recipeDTO.setNotes(recipe.getNotes());
        return recipeDTO;
    }
}
