-- V1__Initial_schema.sql

-- Create table for ingredients
CREATE TABLE ingredients (
                             id VARCHAR(255) NOT NULL,
                             name VARCHAR(255),
                             optional BOOLEAN NOT NULL,
                             quantity FLOAT NOT NULL,
                             type VARCHAR(255),
                             unit VARCHAR(255),
                             PRIMARY KEY (id)
);

-- Create table for recipes
CREATE TABLE recipes (
                         id VARCHAR(255) NOT NULL,
                         name VARCHAR(255),
                         notes VARCHAR(255),
                         instructions TEXT,
                         photo VARCHAR(255),
                         PRIMARY KEY (id)
);

CREATE TABLE tags (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE recipes_tags (
    recipes_id VARCHAR(255) NOT NULL,
    tags_id VARCHAR(255) NOT NULL
);

-- Create the junction table for many-to-many relationship between recipes and ingredients
CREATE TABLE recipes_ingredients (
                                     recipes_id VARCHAR(255) NOT NULL,
                                     ingredients_id VARCHAR(255) NOT NULL
);

-- Unique constraint to ensure each ingredient is unique within the scope of the junction table
ALTER TABLE recipes_ingredients ADD CONSTRAINT unique_ingredient_in_recipe_ingredient UNIQUE (ingredients_id);
-- Foreign key constraint to link ingredients in the junction table to the ingredients table
ALTER TABLE recipes_ingredients ADD CONSTRAINT fk_recipes_ingredients_ingredient FOREIGN KEY (ingredients_id) REFERENCES ingredients;
-- Foreign key constraint to link recipes in the junction table to the recipes table
ALTER TABLE recipes_ingredients ADD CONSTRAINT fk_recipes_ingredients_recipe FOREIGN KEY (recipes_id) REFERENCES recipes;
-- Foreign key constraint to link tags in the junction table to the tags table
ALTER TABLE recipes_tags ADD CONSTRAINT fk_recipe_tags_tag FOREIGN KEY (tags_id) REFERENCES tags;