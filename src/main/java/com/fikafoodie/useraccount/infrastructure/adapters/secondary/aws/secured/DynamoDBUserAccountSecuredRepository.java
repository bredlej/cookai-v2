package com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.secured;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.api.UserAccountControllerPublicAPI;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.DynamoDBUserAccountTableProperties;
import com.fikafoodie.useraccount.infrastructure.entities.DynamoDBUserAccountEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@RequestScoped
@DynamoDB
public class DynamoDBUserAccountSecuredRepository implements UserAccountSecuredRepositoryPort, DynamoDBUserAccountTableProperties {

    @Inject
    JsonWebToken jwt;

    private final DynamoDbTable<DynamoDBUserAccountEntity> userAccountsTable;

    @Inject
    public DynamoDBUserAccountSecuredRepository(@NotNull DynamoDbEnhancedClient dynamoDbEnhancedClient, @ConfigProperty(name = "aws.dynamodb.fikafoodie.useraccounts.table") String userAccountsTableName) {
        this.userAccountsTable = dynamoDbEnhancedClient.table(userAccountsTableName, TableSchema.fromBean(DynamoDBUserAccountEntity.class));
    }

    @Override
    public Optional<UserAccount> getUserAccount() throws UserAccountNotFoundException {
        if (jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM) == null) {
            throw new UserAccountNotFoundException("User account not found");
        }
        return Optional.of(
                getUserAccount(
                        new UserAccount.Name(
                                jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM)
                        )));
    }

    public UserAccount getUserAccount(UserAccount.Name name) {
        DynamoDBUserAccountEntity userAccountEntity = userAccountsTable.getItem(r -> r.key(k -> k.partitionValue(name.value())));
        return DynamoDBUserAccountEntity.toDomain(userAccountEntity);
    }
}
