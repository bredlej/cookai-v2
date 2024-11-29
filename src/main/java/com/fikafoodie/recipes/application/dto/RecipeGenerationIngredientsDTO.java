package com.fikafoodie.recipes.application.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.util.List;

@RegisterForReflection
@Data
public class RecipeGenerationIngredientsDTO {
    List<String> ingredients;
}
