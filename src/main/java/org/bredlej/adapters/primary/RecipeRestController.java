package org.bredlej.adapters.primary;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bredlej.domain.entities.Recipe;
import org.bredlej.domain.exceptions.EntityNotFoundException;
import org.bredlej.domain.ports.RecipeServicePort;

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
            Recipe recipe = recipeService.getRecipeById(id);
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
        recipe.setId(id);
        Recipe savedRecipe = recipeService.addRecipe(recipe);
        return Response.created(URI.create("/recipes/"+recipe.getId())).entity(savedRecipe).build();
    }
}