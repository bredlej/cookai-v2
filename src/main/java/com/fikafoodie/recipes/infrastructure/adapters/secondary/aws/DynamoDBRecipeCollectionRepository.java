package com.fikafoodie.recipes.infrastructure.adapters.secondary.aws;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.recipes.domain.aggregates.RecipeCollection;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeCollectionRepositoryPort;
import com.fikafoodie.recipes.infrastructure.entities.DynamoDBRecipeEntity;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.api.UserAccountControllerPublicAPI;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.time.LocalDateTime;
import java.util.UUID;

import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.keyEqualTo;

@RequestScoped
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
    public RecipeCollection getRecipeCollectionOfUser() throws UserAccountNotFoundException {
        String owner = jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM);
        if (owner == null) {
            throw new UserAccountNotFoundException("User account not found");
        }

        SdkIterable<Page<DynamoDBRecipeEntity>> recipesOfUser = recipesTable.query(r -> r.queryConditional(keyEqualTo(k -> k.partitionValue(owner))));
        PageIterable<DynamoDBRecipeEntity> queryResponse = PageIterable.create(recipesOfUser);

        RecipeCollection recipeCollection = new RecipeCollection();
        for (var item : queryResponse.items()) {
            Recipe recipe = DynamoDBRecipeEntity.toDomain(item);//parseRecipeFromItem(item);
            recipeCollection.addRecipe(recipe);
        }

        return recipeCollection;
    }

    @Override
    public void addRecipeToCollection(Recipe recipe) {
        DynamoDBRecipeEntity item = DynamoDBRecipeEntity.fromDomain(recipe);
        item.setOwnerId(jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM));
        item.setRecipeId(UUID.randomUUID().toString());
        item.setCreatedAt(LocalDateTime.now());

        recipesTable.putItem(item);
    }
}
