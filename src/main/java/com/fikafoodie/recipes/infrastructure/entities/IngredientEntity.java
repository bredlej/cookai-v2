package com.fikafoodie.recipes.infrastructure.entities;

import com.fikafoodie.recipes.domain.valueobjects.Ingredient;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity(name = "ingredients")
public class IngredientEntity extends PanacheEntity {

    public String name;
    public double quantity;
    public String unit;
    public boolean optional;
    public String type;

    public static IngredientEntity fromDomain(Ingredient ingredient) {
        IngredientEntity ingredientEntity = new IngredientEntity();
        ingredientEntity.name = ingredient.getName().value();
        ingredientEntity.quantity = ingredient.getQuantity().value();
        ingredientEntity.unit = ingredient.getUnit().value();
        ingredientEntity.optional = ingredient.isOptional();
        ingredientEntity.type = ingredient.getType().value();
        return ingredientEntity;
    }

    public Ingredient toDomain() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(new Ingredient.Name(name));
        ingredient.setQuantity(new Ingredient.Quantity(quantity));
        ingredient.setUnit(new Ingredient.Unit(unit));
        ingredient.setOptional(optional);
        ingredient.setType(new Ingredient.Type(type));
        return ingredient;
    }
}
