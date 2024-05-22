package com.fikafoodie.infrastructure.entities;

import com.fikafoodie.domain.entities.Recipe;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;

@Entity(name = "recipes")
@Data
@EqualsAndHashCode(callSuper = true)
public class RecipeEntity extends PanacheEntityBase {

    private static final String DELIMETER = "\n";
    @Id
    private String id;
    private String name;
    @OneToMany
    private List<IngredientEntity> ingredients;
    private String instructions;
    private String tags;
    private String photo;
    private String notes;

    public static RecipeEntity fromDomain(Recipe recipe) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setId(recipe.getId().value());
        return recipeEntity.update(recipe);
    }

    public RecipeEntity update(Recipe recipe) {
        this.setName(recipe.getName().value());
        this.setIngredients(recipe.getIngredients().value().stream().map(IngredientEntity::fromDomain).peek(p -> p.persist()).toList());
        this.setInstructions(String.join(DELIMETER, recipe.getInstructions().value()));
        this.setTags(String.join(DELIMETER, recipe.getTags().value()));
        this.setPhoto(recipe.getPicture().value());
        this.setNotes(recipe.getNotes().value());
        return this;
    }

    public Recipe toDomain() {
        Recipe recipe = new Recipe();
        recipe.setId(new Recipe.Id(id));
        recipe.setName(new Recipe.Name(name));
        recipe.setIngredients(new Recipe.Ingredients(ingredients.stream().map(IngredientEntity::toDomain).toList()));
        recipe.setInstructions(new Recipe.Instructions(Arrays.stream(instructions.split(DELIMETER)).toList()));
        recipe.setTags(new Recipe.Tags(Arrays.stream(tags.split(DELIMETER)).toList()));
        recipe.setPicture(new Recipe.Picture(photo));
        recipe.setNotes(new Recipe.Notes(notes));
        return recipe;
    }
}
