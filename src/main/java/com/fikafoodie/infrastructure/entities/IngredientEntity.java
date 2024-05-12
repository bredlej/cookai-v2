package com.fikafoodie.infrastructure.entities;

import com.fikafoodie.domain.valueobjects.Ingredient;
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
        ingredientEntity.name = ingredient.getName();
        ingredientEntity.quantity = ingredient.getQuantity();
        ingredientEntity.unit = ingredient.getUnit();
        ingredientEntity.optional = ingredient.isOptional();
        ingredientEntity.type = ingredient.getType();
        return ingredientEntity;
    }

    public Ingredient toDomain() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setQuantity(quantity);
        ingredient.setUnit(unit);
        ingredient.setOptional(optional);
        ingredient.setType(type);
        return ingredient;
    }
}
