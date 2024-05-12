package org.bredlej.infrastructure.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import org.bredlej.domain.entities.Recipe;

import java.util.Arrays;
import java.util.List;

@Entity(name = "recipes")
@Data
public class RecipeEntity extends PanacheEntityBase {
    @Id
    private String id;
    private String name;
    @OneToMany
    private List<IngredientEntity> ingredients;
    private String instructions;
    @OneToMany
    private List<TagEntity> tags;
    private String photo;
    private String notes;

    public Recipe toDomain() {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName(name);
        recipe.setIngredients(ingredients.stream().map(IngredientEntity::toDomain).toList());
        recipe.setInstructions(Arrays.stream(instructions.split("\n")).toList());
        recipe.setTags(tags.stream().map(TagEntity::toDomain).toList());
        recipe.setPhoto(photo);
        recipe.setNotes(notes);
        return recipe;
    }

    public static RecipeEntity fromDomain(Recipe recipe) {
        RecipeEntity recipeEntity = new RecipeEntity();
        recipeEntity.setId(recipe.getId());
        recipeEntity.setName(recipe.getName());
        recipeEntity.setIngredients(recipe.getIngredients().stream().map(IngredientEntity::fromDomain).toList());
        recipeEntity.setInstructions(String.join("\n", recipe.getInstructions()));
        recipeEntity.setTags(recipe.getTags().stream().map(TagEntity::fromDomain).toList());
        recipeEntity.setPhoto(recipe.getPhoto());
        recipeEntity.setNotes(recipe.getNotes());
        return recipeEntity;
    }
}
