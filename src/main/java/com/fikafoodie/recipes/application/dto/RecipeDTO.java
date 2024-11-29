package com.fikafoodie.recipes.application.dto;

import com.fikafoodie.recipes.domain.entities.Recipe;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.util.List;

@RegisterForReflection
@Data
public class RecipeDTO {
    private String id;
    private String name;
    private String summary;
    private List<IngredientDTO> ingredients;
    private List<String> instructions;
    private List<String> tags;
    private String photo;
    private String notes;
    private String prompt;

    public static RecipeDTO fromDomain(Recipe recipe) {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(recipe.getId().value());
        recipeDTO.setSummary(recipe.getSummary().value());
        recipeDTO.setName(recipe.getName().value());
        recipeDTO.setIngredients(IngredientDTO.fromDomain(recipe.getIngredients().value()));
        recipeDTO.setInstructions(recipe.getInstructions().value());
        recipeDTO.setTags(recipe.getTags().value());
        recipeDTO.setPhoto(recipe.getPicture().value());
        recipeDTO.setNotes(recipe.getNotes().value());
        return recipeDTO;
    }

    public static Recipe toDomain(RecipeDTO recipeDTO) {
        Recipe recipe = new Recipe();
        recipe.setId(new Recipe.Id(recipeDTO.getId()));
        recipe.setName(new Recipe.Name(recipeDTO.getName()));
        recipe.setSummary(new Recipe.Summary(recipeDTO.getSummary()));
        recipe.setIngredients(new Recipe.Ingredients(IngredientDTO.toDomain(recipeDTO.getIngredients())));
        recipe.setInstructions(new Recipe.Instructions(recipeDTO.getInstructions()));
        recipe.setTags(new Recipe.Tags(recipeDTO.getTags()));
        recipe.setPicture(new Recipe.Picture(recipeDTO.getPhoto()));
        recipe.setNotes(new Recipe.Notes(recipeDTO.getNotes()));
        recipe.setPrompt(new Recipe.ImageGenerationPrompt(recipeDTO.getPrompt()));
        return recipe;
    }
}
