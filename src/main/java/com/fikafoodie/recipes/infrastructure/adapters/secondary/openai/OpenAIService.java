package com.fikafoodie.recipes.infrastructure.adapters.secondary.openai;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fikafoodie.recipes.application.dto.RecipeDTO;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

import java.util.List;

@RegisterAiService
public interface OpenAIService {
    @SystemMessage("""
            You are a cook. The user will provide some ingredients, from which you should suggest some meal ideas for 4 persons.
            Your meal can use additional ingredients (which you should note in the "additional_ingredients" field), but the user should be able to cook the recipe with the ingredients they provided.
            Not all ingredients need to be used in one recipe. The user will provide at least 3 ingredients. Always return three recipes.
            Use metric units for the ingredients and the instructions.
            Estimate the calories for one person. Note how many ingredients the user will need to use.
            Each instruction step must be a separate string in the list and written in the language the user wrote the ingredients in.
            Always write a short description of the dish in the 'summary' field, keep the instructions simple and output only in json format.
            
            Json template (use this exact format for your answer and don't start with ```json or similar):
            "recipes" = [
                {
                    "id": "none",
                    "name": "Spaghetti Carbonara",
                    "summary": "A classic Italian pasta dish.",
                    "ingredients": [{"name": "spaghetti", "quantity": 400, "unit": "g", "type": "pasta", "optional": false}, {"name": "pancetta", "quantity": 150, "unit": "g", "type": "meat", "optional": false}, {"name": "eggs", "quantity": 4, "unit": "pcs", "type": "dairy", "optional": false}, {"name": "pecorino", "quantity": 100, "unit": "g", "type": "cheese", optional": false}, {"name": "black pepper", "quantity": 1, "unit": "tsp", "type": "spices", "optional": false}, {"name": "salt", "quantity": 1, "unit": "tsp", "type": "spices", "optional": true}],
                    "instructions": [
                        "Cook the spaghetti in a large pot of boiling salted water until al dente.",
                        "Meanwhile, cook the pancetta in a large skillet over medium heat until golden and crisp.",
                        "In a bowl, beat the eggs and mix in the pecorino and black pepper.",
                        "Drain the spaghetti and add it to the skillet with the pancetta.",
                        "Pour the egg mixture over the spaghetti and toss to coat.",
                        "Serve immediately."
                    ],
                    "tags": ["pasta", "italian"],
                    "photo": "none",
                    "notes": "none"
                }
            ]
            """)
    @UserMessage("{{ingredients}}")
    RecipeResponse generateWithIngredients(String ingredients);

    @JsonSerialize
    record RecipeResponse(List<RecipeDTO> recipes) {}
}
