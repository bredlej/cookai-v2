package com.fikafoodie.adapters.primary;

import com.fikafoodie.domain.entities.Recipe;
import com.fikafoodie.domain.exceptions.EntityNotFoundException;
import com.fikafoodie.domain.ports.RecipeServicePort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@RequestScoped
@Path("/recipes")
public class RecipeRestController {
    @Inject
    RecipeServicePort recipeService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecipeById(@PathParam("id") String id) {
        try {
            Recipe recipe = recipeService.getRecipeById(new Recipe.Id(id));
            return Response.ok(recipe).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Recipe not found").build();
        }
    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRecipe(@PathParam("id") String id, Recipe recipe) {
        recipe.setId(new Recipe.Id(id));
        Recipe savedRecipe = recipeService.addRecipe(recipe);
        return Response.created(URI.create("/recipes/" + recipe.getId())).entity(savedRecipe).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRecipe(@PathParam("id") String id, Recipe recipe) {
        recipe.setId(new Recipe.Id(id));
        try {
            Recipe updatedRecipe = recipeService.updateRecipe(recipe);
            return Response.ok(updatedRecipe).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Recipe not found").build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRecipeById(@PathParam("id") String id) {
        try {
            recipeService.deleteRecipeById(new Recipe.Id(id));
            return Response.noContent().build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("Recipe not found").build();
        }
    }

}