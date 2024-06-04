package com.fikafoodie.recipes.infrastructure.converters;

import com.fikafoodie.recipes.domain.valueobjects.Ingredient;
import com.fikafoodie.recipes.infrastructure.entities.DynamoDBRecipeEntity;

public class IngredientConverter {

    public static Ingredient convertToDomainIngredient(DynamoDBRecipeEntity.Ingredient ingredient) {
        Ingredient ingredientDomain = new Ingredient();
        ingredientDomain.setName(new Ingredient.Name(ingredient.getName()));
        ingredientDomain.setQuantity(new Ingredient.Quantity(ingredient.getQuantity()));
        ingredientDomain.setUnit(new Ingredient.Unit(ingredient.getUnit()));
        ingredientDomain.setOptional(ingredient.isOptional());
        ingredientDomain.setType(new Ingredient.Type(ingredient.getType()));
        return ingredientDomain;
    }

    public static DynamoDBRecipeEntity.Ingredient convertToDynamoDBIngredient(Ingredient ingredient) {
        DynamoDBRecipeEntity.Ingredient ingredientEntity = new DynamoDBRecipeEntity.Ingredient();
        ingredientEntity.setName(ingredient.getName().value());
        ingredientEntity.setQuantity(ingredient.getQuantity().value());
        ingredientEntity.setUnit(ingredient.getUnit().value());
        ingredientEntity.setOptional(ingredient.isOptional());
        ingredientEntity.setType(ingredient.getType().value());
        return ingredientEntity;
    }
}
