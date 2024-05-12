package com.fikafoodie.domain.valueobjects;

import lombok.Data;

@Data
public class Ingredient {
    private String name;
    private double quantity;
    private String unit;
    private boolean optional;
    private String type;
}
