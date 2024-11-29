package com.fikafoodie.recipes.infrastructure.adapters.secondary.aws;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.recipes.application.exceptions.RecipeCollectionNotFoundException;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeNotFoundException;
import com.fikafoodie.recipes.infrastructure.entities.DynamoDBRecipeEntity;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.api.UserAccountControllerPublicAPI;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

@ApplicationScoped
@DynamoDB
public class DynamoDBRecipeCollectionRepository implements RecipeCollectionRepositoryPort {

    private final JsonWebToken jwt;
    private final DynamoDbTable<DynamoDBRecipeEntity> recipesTable;

    @Inject
    public DynamoDBRecipeCollectionRepository(@NotNull DynamoDbEnhancedClient dynamoDbEnhancedClient, JsonWebToken jwt, @ConfigProperty(name = "aws.dynamodb.fikafoodie.recipes.table") String recipesTableName) {
        this.recipesTable = dynamoDbEnhancedClient.table(recipesTableName, TableSchema.fromClass(DynamoDBRecipeEntity.class));
        this.jwt = jwt;
    }

    @Override
    public RecipeCollection getRecipeCollectionOfUser() throws RecipeCollectionNotFoundException {
        String owner = jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM);
        if (owner == null) {
            throw new RecipeCollectionNotFoundException("Cannot access the recipe collection of the user");
        }

        SdkIterable<Page<DynamoDBRecipeEntity>> recipesOfUser = recipesTable.query(r -> r.queryConditional(keyEqualTo(k -> k.partitionValue(owner))));
        PageIterable<DynamoDBRecipeEntity> queryResponse = PageIterable.create(recipesOfUser);

        RecipeCollection recipeCollection = new RecipeCollection();
        for (var item : queryResponse.items()) {
            Recipe recipe = DynamoDBRecipeEntity.toDomain(item);
            recipeCollection.addRecipe(recipe);
        }

        return recipeCollection;
    }

    @Override
    public void addRecipeToCollection(Recipe recipe) throws RecipeCollectionNotFoundException {
        String owner = jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM);
        if (owner == null) {
            throw new RecipeCollectionNotFoundException("Cannot access the recipe collection of the user");
        }

        DynamoDBRecipeEntity item = DynamoDBRecipeEntity.fromDomain(recipe);
        item.setOwnerId(jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM));

        recipesTable.putItem(item);
    }

    @Override
    public void updateRecipeInCollection(Recipe recipe) throws RecipeNotFoundException, RecipeCollectionNotFoundException {
        String owner = jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM);
        if (owner == null) {
            throw new RecipeCollectionNotFoundException("Cannot access the recipe collection of the user");
        }

        DynamoDBRecipeEntity item = recipesTable.getItem(Key.builder().partitionValue(owner).sortValue(recipe.getId().value()).build());
        if (item == null) {
            throw new RecipeNotFoundException("Recipe not found in collection");
        }

        DynamoDBRecipeEntity updatedItem = DynamoDBRecipeEntity.fromDomain(recipe);
        updatedItem.setOwnerId(owner);

        recipesTable.updateItem(updatedItem);
    }
}
