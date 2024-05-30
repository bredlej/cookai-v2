package com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.open;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountPublicRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.DynamoDBUserAccountTableProperties;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestScoped
@DynamoDB
public class DynamoDBUserAccountPublicRepository implements UserAccountPublicRepositoryPort, DynamoDBUserAccountTableProperties {

    private final DynamoDbClient dynamoDbClient;

    @Inject
    @ConfigProperty(name = "aws.dynamodb.fikafoodie.useraccount.table")
    String userAccountTableName;

    @Inject
    public DynamoDBUserAccountPublicRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void createAccount(UserAccount userAccount) {
        dynamoDbClient.putItem(putRequest(userAccount));
    }

    @Override
    public void setAccountCredits(UserAccount.Name name, UserAccount.Credits credits) {
        dynamoDbClient.updateItem(updateCreditRequest(name, credits));
    }

    @Override
    public void setAccountStatus(UserAccount.Name name, UserAccount.Status status) {
        dynamoDbClient.updateItem(updateStatusRequest(name, status));
    }

    @Override
    public Optional<UserAccount.Status> getAccountStatus(UserAccount.Name name) {
        var item = dynamoDbClient.getItem(getRequest(name));
        if (item != null && item.hasItem()) {
            return Optional.of(UserAccount.Status.valueOf(item.item().get(USER_ACCOUNT_STATUS_COLUMN).s()));
        }
        return Optional.empty();
    }

    private GetItemRequest getRequest(UserAccount.Name name) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_ACCOUNT_NAME_COLUMN, AttributeValue.builder().s(name.value()).build());

        return GetItemRequest.builder()
                .tableName(userAccountTableName)
                .key(key)
                .build();
    }

    private PutItemRequest putRequest(UserAccount userAccount) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_ACCOUNT_OWNERID_COLUMN, AttributeValue.builder().s(userAccount.getId().value()).build());
        item.put(USER_ACCOUNT_NAME_COLUMN, AttributeValue.builder().s(userAccount.getName().value()).build());
        item.put(USER_ACCOUNT_EMAIL_COLUMN, AttributeValue.builder().s(userAccount.getEmail().value()).build());
        item.put(USER_ACCOUNT_CREDITS_COLUMN, AttributeValue.builder().n(String.valueOf(userAccount.creditBalance().value())).build());
        item.put(USER_ACCOUNT_STATUS_COLUMN, AttributeValue.builder().s(userAccount.getStatus().name()).build());

        return PutItemRequest.builder()
                .tableName(userAccountTableName)
                .item(item)
                .build();
    }

    private UpdateItemRequest updateCreditRequest(UserAccount.Name name, UserAccount.Credits credits) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_ACCOUNT_NAME_COLUMN, AttributeValue.builder().s(name.value()).build());
        Map<String, AttributeValueUpdate> valueUpdateMap = new HashMap<>();
        valueUpdateMap.put(USER_ACCOUNT_CREDITS_COLUMN, AttributeValueUpdate.builder().value(AttributeValue.builder().n(String.valueOf(credits.value())).build()).action(AttributeAction.PUT).build());
        return UpdateItemRequest.builder()
                .tableName(userAccountTableName)
                .key(item)
                .attributeUpdates(valueUpdateMap)
                .build();
    }

    private UpdateItemRequest updateStatusRequest(UserAccount.Name name, UserAccount.Status status) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_ACCOUNT_NAME_COLUMN, AttributeValue.builder().s(name.value()).build());
        Map<String, AttributeValueUpdate> valueUpdateMap = new HashMap<>();
        valueUpdateMap.put(USER_ACCOUNT_STATUS_COLUMN, AttributeValueUpdate.builder().value(AttributeValue.builder().s(status.name()).build()).action(AttributeAction.PUT).build());
        return UpdateItemRequest.builder()
                .tableName(userAccountTableName)
                .key(item)
                .attributeUpdates(valueUpdateMap)
                .build();
    }


}
