package com.fikafoodie.infrastructure.entities;

import com.fikafoodie.domain.entities.Recipe;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Entity(name = "recipes")
@Data
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
        recipeEntity.setId(recipe.getId());
        return recipeEntity.update(recipe);
    }

    public RecipeEntity update(Recipe recipe) {
        this.setName(recipe.getName());
        this.setIngredients(recipe.getIngredients().stream().map(IngredientEntity::fromDomain).toList());
        this.setInstructions(String.join(DELIMETER, recipe.getInstructions()));
        this.setTags(String.join(DELIMETER, recipe.getTags()));
        this.setPhoto(recipe.getPhoto());
        this.setNotes(recipe.getNotes());
        return this;
    }

    public Recipe toDomain() {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName(name);
        recipe.setIngredients(ingredients.stream().map(IngredientEntity::toDomain).toList());
        recipe.setInstructions(Arrays.stream(instructions.split(DELIMETER)).toList());
        recipe.setTags(Arrays.stream(tags.split(DELIMETER)).toList());
        recipe.setPhoto(photo);
        recipe.setNotes(notes);
        return recipe;
    }
}
