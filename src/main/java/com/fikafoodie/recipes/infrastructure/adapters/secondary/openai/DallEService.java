package com.fikafoodie.recipes.infrastructure.adapters.secondary.openai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface DallEService {
    @SystemMessage("""
            You're a food illustrator. I'll provide you with a recipe name, description and ingredients. Please draw in the style of New York Times comics. Don't write any text in the picture. Generate the picture and return an url to it.
            """)
    @UserMessage("Name: {{recipeName}} Description: {{recipeDescription}} Ingredients: {{ingredients}}")
    String generateIllustration(String recipeName, String recipeDescription, String ingredients);
}
