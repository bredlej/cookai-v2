package com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.open;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountPublicRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.DynamoDBUserAccountTableProperties;
import com.fikafoodie.useraccount.infrastructure.entities.DynamoDBUserAccountEntity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.LocalDateTime;
import java.util.Optional;

@RequestScoped
@DynamoDB
public class DynamoDBUserAccountPublicRepository implements UserAccountPublicRepositoryPort, DynamoDBUserAccountTableProperties {

    private final DynamoDbTable<DynamoDBUserAccountEntity> userAccountsTable;

    @Inject
    public DynamoDBUserAccountPublicRepository(@NotNull DynamoDbEnhancedClient dynamoDbEnhancedClient, @ConfigProperty(name = "aws.dynamodb.fikafoodie.useraccounts.table") String userAccountsTableName) {
        this.userAccountsTable = dynamoDbEnhancedClient.table(userAccountsTableName, TableSchema.fromBean(DynamoDBUserAccountEntity.class));
    }

    @Override
    public void createAccount(UserAccount userAccount) {

        DynamoDBUserAccountEntity entity = DynamoDBUserAccountEntity.fromDomain(userAccount);
        entity.setCreatedAt(LocalDateTime.now());

        userAccountsTable.putItem(entity);
    }

    @Override
    public void setAccountCredits(UserAccount.Name name, UserAccount.Credits credits) {
        DynamoDBUserAccountEntity userAccountEntity = userAccountsTable.getItem(r -> r.key(k -> k.partitionValue(name.value())));
        userAccountEntity.setCredits(credits.value());
        userAccountsTable.updateItem(userAccountEntity);
    }

    @Override
    public void setAccountStatus(UserAccount.Name name, UserAccount.Status status) {
        DynamoDBUserAccountEntity userAccountEntity = userAccountsTable.getItem(r -> r.key(k -> k.partitionValue(name.value())));
        userAccountEntity.setStatus(status.name());
        userAccountsTable.updateItem(userAccountEntity);
    }

    @Override
    public Optional<UserAccount.Status> getAccountStatus(UserAccount.Name name) {
        DynamoDBUserAccountEntity userAccountEntity = userAccountsTable.getItem(r -> r.key(k -> k.partitionValue(name.value())));
        if (userAccountEntity != null) {
            return Optional.of(UserAccount.Status.valueOf(userAccountEntity.getStatus()));
        }
        return Optional.empty();
    }
}
