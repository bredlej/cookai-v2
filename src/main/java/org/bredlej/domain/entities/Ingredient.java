package org.bredlej.domain.entities;

import lombok.Data;

@Data
public class Ingredient {
    private String id;
    private String name;
    private double quantity;
    private String unit;
    private boolean optional;
    private String type;
}
