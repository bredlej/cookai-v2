package com.fikafoodie.recipes.application.dto;

import com.fikafoodie.recipes.domain.valueobjects.Ingredient;
import lombok.Data;

import java.util.List;

@Data
public class IngredientDTO {
    private String name;
    private double quantity;
    private String unit;
    private boolean optional;
    private String type;

    public static List<IngredientDTO> fromDomain(List<Ingredient> ingredients) {
        return ingredients.stream().map(IngredientDTO::fromDomain).toList();
    }

    public static IngredientDTO fromDomain(Ingredient ingredient) {
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName(ingredient.getName().value());
        ingredientDTO.setQuantity(ingredient.getQuantity().value());
        ingredientDTO.setUnit(ingredient.getUnit().value());
        ingredientDTO.setOptional(ingredient.isOptional());
        ingredientDTO.setType(ingredient.getType().value());
        return ingredientDTO;
    }

    public Ingredient toDomain() {
        return new Ingredient(
                new Ingredient.Name(name),
                new Ingredient.Quantity(quantity),
                new Ingredient.Unit(unit),
                optional,
                new Ingredient.Type(type)
        );
    }
    public static List<Ingredient> toDomain(List<IngredientDTO> ingredientDTOs) {
        return ingredientDTOs.stream().map(IngredientDTO::toDomain).toList();
    }
}
