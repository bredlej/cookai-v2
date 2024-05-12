package com.fikafoodie.infrastructure.entities;

import com.fikafoodie.domain.entities.Ingredient;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "ingredients")
public class IngredientEntity extends PanacheEntityBase {

    @Id
    public String id;
    public String name;
    public double quantity;
    public String unit;
    public boolean optional;
    public String type;

    public Ingredient toDomain() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setQuantity(quantity);
        ingredient.setUnit(unit);
        ingredient.setOptional(optional);
        ingredient.setType(type);
        return ingredient;
    }

    public static IngredientEntity fromDomain(Ingredient ingredient) {
        IngredientEntity ingredientEntity = new IngredientEntity();
        ingredientEntity.id = ingredient.getId();
        ingredientEntity.name = ingredient.getName();
        ingredientEntity.quantity = ingredient.getQuantity();
        ingredientEntity.unit = ingredient.getUnit();
        ingredientEntity.optional = ingredient.isOptional();
        ingredientEntity.type = ingredient.getType();
        return ingredientEntity;
    }
}
