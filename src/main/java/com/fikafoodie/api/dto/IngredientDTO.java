package com.fikafoodie.api.dto;

import com.fikafoodie.domain.valueobjects.Ingredient;
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
        ingredientDTO.setName(ingredient.getName());
        ingredientDTO.setQuantity(ingredient.getQuantity());
        ingredientDTO.setUnit(ingredient.getUnit());
        ingredientDTO.setOptional(ingredient.isOptional());
        ingredientDTO.setType(ingredient.getType());
        return ingredientDTO;
    }
}
