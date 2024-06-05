package com.fikafoodie.recipes.infrastructure.entities;

import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.infrastructure.adapters.secondary.aws.DynamoDBRecipesTableProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@RegisterForReflection
@DynamoDbBean
public class DynamoDBRecipeEntity {
    private String ownerId;
    private String recipeId;
    private String name;
    private String summary;
    private List<Ingredient> ingredients;
    private List<String> instructions;
    private List<String> tags;
    private String picture;
    private String notes;
    private LocalDateTime createdAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_OWNERID_COLUMN)
    public String getOwnerId() {
        return ownerId;
    }

    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_RECIPEID_COLUMN)
    public String getRecipeId() {
        return recipeId;
    }

    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_SUMMARY_COLUMN)
    public String getSummary() {
        return summary;
    }

    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_INGREDIENTS_COLUMN)
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_INSTRUCTIONS_COLUMN)
    public List<String> getInstructions() {
        return instructions;
    }

    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_TAGS_COLUMN)
    public List<String> getTags() {
        return tags;
    }

    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_PICTURE_COLUMN)
    public String getPicture() {
        return picture;
    }

    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_NOTES_COLUMN)
    public String getNotes() {
        return notes;
    }

    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_NAME_COLUMN)
    public String getName() {
        return name;
    }

    @DynamoDbAttribute(DynamoDBRecipesTableProperties.RECIPE_COLLECTION_CREATED_AT_COLUMN)
    @DynamoDbSortKey
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Setter
    @Getter
    @RegisterForReflection
    @DynamoDbBean
    public static class Ingredient {
        private String name;
        private double quantity;
        private String unit;
        private boolean optional;
        private String type;
    }

    public static DynamoDBRecipeEntity fromDomain(Recipe recipe) {
        DynamoDBRecipeEntity entity = new DynamoDBRecipeEntity();
        entity.setRecipeId(recipe.getId().value());
        entity.setName(recipe.getName().value());
        entity.setSummary(recipe.getSummary().value());
        entity.setIngredients(recipe.getIngredients().value().stream()
                .map(ingredient -> {
                    Ingredient i = new Ingredient();
                    i.setName(ingredient.getName().value());
                    i.setQuantity(ingredient.getQuantity().value());
                    i.setUnit(ingredient.getUnit().value());
                    i.setOptional(ingredient.isOptional());
                    i.setType(ingredient.getType().value());
                    return i;
                })
                .toList());
        entity.setInstructions(recipe.getInstructions().value());
        entity.setTags(recipe.getTags().value());
        entity.setPicture(recipe.getPicture().value());
        entity.setNotes(recipe.getNotes().value());
        return entity;
    }
}
