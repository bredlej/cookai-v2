package com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.secured;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.api.UserAccountControllerPublicAPI;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.DynamoDBUserAccountTableProperties;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequestScoped
@DynamoDB
public class DynamoDBUserAccountSecuredRepository implements UserAccountSecuredRepositoryPort, DynamoDBUserAccountTableProperties {

    @Inject
    JsonWebToken jwt;

    @Inject
    @ConfigProperty(name = "aws.dynamodb.fikafoodie.useraccount.table")
    String userAccountTableName;

    private final DynamoDbClient dynamoDbClient;

    @Inject
    public DynamoDBUserAccountSecuredRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public Optional<UserAccount> getUserAccount() throws UserAccountNotFoundException {
        if (jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM) != null) {
            throw new UserAccountNotFoundException("User account not found");
        }
        return Optional.of(
                getUserAccount(
                        new UserAccount.Name(
                                jwt.getClaim(UserAccountControllerPublicAPI.COGNITO_USERNAME_CLAIM)
                        )));
    }

    public UserAccount getUserAccount(UserAccount.Name name) {
        var item = dynamoDbClient.getItem(getRequest(name));
        UserAccount userAccount = new UserAccount();
        if (item != null && item.hasItem()) {
            userAccount.setId(new UserAccount.Id(item.item().get(USER_ACCOUNT_OWNERID_COLUMN).s()));
            userAccount.setName(new UserAccount.Name(item.item().get(USER_ACCOUNT_NAME_COLUMN).s()));
            userAccount.setEmail(new UserAccount.Email(item.item().get(USER_ACCOUNT_EMAIL_COLUMN).s()));
            userAccount.setCredits(new UserAccount.Credits(Integer.parseInt(item.item().get(USER_ACCOUNT_CREDITS_COLUMN).n())));
        }
        return userAccount;
    }

    private GetItemRequest getRequest(UserAccount.Name name) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put(USER_ACCOUNT_NAME_COLUMN, AttributeValue.builder().s(name.value()).build());

        return GetItemRequest.builder()
                .tableName(userAccountTableName)
                .key(key)
                .build();
    }
}
