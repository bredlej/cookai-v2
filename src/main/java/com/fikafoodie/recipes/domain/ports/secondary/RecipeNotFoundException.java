package com.fikafoodie.recipes.domain.ports.secondary;

public class RecipeNotFoundException extends Exception {
    public RecipeNotFoundException(String message) {
        super(message);
    }
}
