package com.fikafoodie.recipes.domain.entities;

import com.fikafoodie.recipes.domain.valueobjects.Ingredient;
import io.vertx.core.cli.annotations.Summary;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Recipe {
    private Id id = new Id("");
    private Name name = new Name("");
    private Summary summary = new Summary("");
    private Ingredients ingredients = new Ingredients(new ArrayList<>());
    private Instructions instructions = new Instructions(new ArrayList<>());
    private Tags tags = new Tags(new ArrayList<>());
    private Picture picture = new Picture("");
    private Notes notes = new Notes("");

    public record Id(String value) {
        public Id {
            if (value == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
        }
    }

    public record Name(String value) {
        public Name {
            if (value == null) {
                throw new IllegalArgumentException("Name cannot be null");
            }
        }
    }

    public record Summary(String value) {
        public Summary {
            if (value == null) {
                throw new IllegalArgumentException("Summary cannot be null");
            }
        }
    }

    public record Ingredients(List<Ingredient> value) {
        public Ingredients {
            if (value == null) {
                throw new IllegalArgumentException("Ingredients cannot be null");
            }
        }
    }

    public record Instructions(List<String> value) {
        public Instructions {
            if (value == null) {
                throw new IllegalArgumentException("Instructions cannot be null");
            }
        }
    }

    public record Tags (List<String> value) {
        public Tags {
            if (value == null) {
                throw new IllegalArgumentException("Tags cannot be null");
            }
        }
    }

    public record Picture(String value) {
        public Picture {
            if (value == null) {
                throw new IllegalArgumentException("Picture cannot be null");
            }
        }
    }

    public record Notes(String value) {
        public Notes {
            if (value == null) {
                throw new IllegalArgumentException("Notes cannot be null");
            }
        }
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name=" + name +
                ", ingredients=" + ingredients +
                ", instructions=" + instructions +
                ", tags=" + tags +
                ", picture=" + picture +
                ", notes=" + notes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipe recipe = (Recipe) o;

        return id.equals(recipe.id);
    }
}