package com.fikafoodie.recipes.infrastructure.adapters.secondary.openai;

import com.fikafoodie.kernel.qualifiers.OpenAI;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeGenerationServicePort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.List;

@RequestScoped
@OpenAI
public class OpenAIRecipeGenerationAdapter implements RecipeGenerationServicePort {

    private final OpenAIService openAIService;

    @Inject
    public OpenAIRecipeGenerationAdapter(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @Override
    public List<Recipe> generateRecipesWithIngredients(List<String> ingredients) {
        return openAIService.generateWithIngredients(String.join(",", ingredients))
                .recipes().stream().map(RecipeDTO::toDomain).toList();
    }
}
