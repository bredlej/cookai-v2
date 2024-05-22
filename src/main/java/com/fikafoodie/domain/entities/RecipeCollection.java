package com.fikafoodie.domain.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeCollection {
    private Id id;
    private Recipes recipes = new Recipes(new ArrayList<>());

    public record Id(String value) {
        public Id {
            if (value == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
        }
    }

    public record Recipes(List<Recipe> value) {
        public Recipes {
            if (value == null) {
                throw new IllegalArgumentException("Recipes cannot be null");
            }
        }
    }
}
