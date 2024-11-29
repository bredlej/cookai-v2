package com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.secured;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.api.UserAccountControllerPublicAPI;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.DynamoDBUserAccountTableProperties;
import com.fikafoodie.useraccount.infrastructure.entities.DynamoDBUserAccountEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@ApplicationScoped
@DynamoDB
public class DynamoDBUserAccountSecuredRepository implements UserAccountSecuredRepositoryPort, DynamoDBUserAccountTableProperties {

    @Inject
    JsonWebToken jwt;

    private final DynamoDbTable<DynamoDBUserAccountEntity> userAccountsTable;

    @Inject
    public DynamoDBUserAccountSecuredRepository(@NotNull DynamoDbEnhancedClient dynamoDbEnhancedClient, @ConfigProperty(name = "aws.dynamodb.fikafoodie.useraccounts.table") String userAccountsTableName) {
        this.userAccountsTable = dynamoDbEnhancedClient.table(userAccountsTableName, TableSchema.fromBean(DynamoDBUserAccountEntity.class));
    }

    public UserAccount getUserAccount(UserAccount.Name name) {
        return DynamoDBUserAccountEntity.toDomain(getUserAccountEntity(name));
    }

    private DynamoDBUserAccountEntity getUserAccountEntity(UserAccount.Name name) {
        return userAccountsTable.getItem(r -> r.key(k -> k.partitionValue(name.value())));
    }

    @Override
    public UserAccount.Credits getCreditBalance() throws UserAccountNotFoundException {
        var name = new UserAccount.Name(jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM));
        if (getUserAccountEntity(name) == null) {
            throw new UserAccountNotFoundException("User account not found");
        }
        return getUserAccount(name).creditBalance();
    }

    @Override
    public void subtractCredits(UserAccount.Credits credits) throws UserAccountNotFoundException {
        var name = new UserAccount.Name(jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM));
        var entity = getUserAccountEntity(name);
        if (entity == null) {
            throw new UserAccountNotFoundException("User account not found");
        }
        entity.setCredits(entity.getCredits() - credits.value());
        if (entity.getCredits() < 0) {
            entity.setCredits(0);
        }
        userAccountsTable.putItem(entity);
    }

    @Override
    public void addCredits(UserAccount.Credits credits) throws UserAccountNotFoundException {
        var name = new UserAccount.Name(jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM));
        var entity = getUserAccountEntity(name);
        if (entity == null) {
            throw new UserAccountNotFoundException("User account not found");
        }
        entity.setCredits(entity.getCredits() + credits.value());
        userAccountsTable.putItem(entity);
    }
}
