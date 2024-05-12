package com.fikafoodie.domain.entities;

import com.fikafoodie.domain.valueobjects.Ingredient;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Recipe {
    private String id;
    private String name;
    private List<Ingredient> ingredients = new ArrayList<>();
    private List<String> instructions = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private String photo;
    private String notes;
}