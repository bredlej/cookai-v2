package com.fikafoodie.recipes.domain.valueobjects;

import lombok.Data;

@Data
public class Ingredient {
    private Name name = new Name("");
    private Quantity quantity = new Quantity(0);
    private Unit unit = new Unit("");
    private boolean optional;
    private Type type = new Type("");

    public record Name(String value) {
        public Name {
            if (value == null) {
                throw new IllegalArgumentException("Name cannot be null");
            }
        }
    }

    public record Quantity(double value) {
        public Quantity {
            if (value < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative");
            }
        }
    }

    public record Unit(String value) {
        public Unit {
            if (value == null) {
                throw new IllegalArgumentException("Unit cannot be null");
            }
        }
    }

    public record Type(String value) {
        public Type {
            if (value == null) {
                throw new IllegalArgumentException("Type cannot be null");
            }
        }
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name=" + name +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", optional=" + optional +
                ", type=" + type +
                '}';
    }
}
