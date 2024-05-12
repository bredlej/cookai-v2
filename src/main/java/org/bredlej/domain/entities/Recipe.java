package org.bredlej.domain.entities;

import lombok.Data;

import java.util.List;

@Data
public class Recipe {
    private String id;
    private String name;
    private List<Ingredient> ingredients = List.of();
    private List<String> instructions = List.of();
    private List<Tag> tags = List.of();
    private String photo;
    private String notes;
}