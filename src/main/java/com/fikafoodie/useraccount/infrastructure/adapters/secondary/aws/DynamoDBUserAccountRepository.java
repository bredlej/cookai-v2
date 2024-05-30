package com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@DynamoDB
@ApplicationScoped
public class DynamoDBUserAccountRepository implements UserAccountRepositoryPort {

    public final static String USER_ACCOUNT_OWNERID_COLUMN = "ownerId";
    public final static String USER_ACCOUNT_NAME_COLUMN = "name";
    public final static String USER_ACCOUNT_EMAIL_COLUMN = "email";
    public final static String USER_ACCOUNT_CREDITS_COLUMN = "credits";
    public final static String USER_ACCOUNT_STATUS_COLUMN = "status";

    private final DynamoDbClient dynamoDbClient;

    @Inject
    @ConfigProperty(name = "aws.dynamodb.fikafoodie.useraccount.table")
    String userAccountTableName;

    @Inject
    public DynamoDBUserAccountRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void createAccount(UserAccount userAccount) {
        dynamoDbClient.putItem(putRequest(userAccount));
    }

    @Override
    public Optional<UserAccount> getUserAccount() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setAccountCredits(UserAccount.Credits credits) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setAccountStatus(UserAccount.Status status) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public UserAccount getUserAccount(UserAccount.Id id) {
        var item = dynamoDbClient.getItem(getRequest(id));
        UserAccount userAccount = new UserAccount();
        if (item != null && item.hasItem()) {
            userAccount.setId(new UserAccount.Id(item.item().get(USER_ACCOUNT_OWNERID_COLUMN).s()));
            userAccount.setName(new UserAccount.Name(item.item().get(USER_ACCOUNT_NAME_COLUMN).s()));
            userAccount.setEmail(new UserAccount.Email(item.item().get(USER_ACCOUNT_EMAIL_COLUMN).s()));
            userAccount.setCredits(new UserAccount.Credits(Integer.parseInt(item.item().get(USER_ACCOUNT_CREDITS_COLUMN).n())));
        }
        return userAccount;
    }

    protected ScanRequest scanRequest() {
        return ScanRequest.builder().tableName(userAccountTableName).build();
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

    private GetItemRequest getRequest(UserAccount.Id ownerId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_ACCOUNT_OWNERID_COLUMN, AttributeValue.builder().s(ownerId.value()).build());

        return GetItemRequest.builder()
                .tableName(userAccountTableName)
                .key(key)
                .build();
    }
}
