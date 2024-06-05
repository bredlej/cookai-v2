package com.fikafoodie.useraccount.infrastructure.entities;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;

import static com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws.DynamoDBUserAccountTableProperties.*;

@Setter
@RegisterForReflection
@DynamoDbBean
public class DynamoDBUserAccountEntity {

    private String ownerId;
    private String name;
    private String email;
    private Integer credits;
    private String status;
    private LocalDateTime createdAt;

    @DynamoDbAttribute(USER_ACCOUNT_OWNERID_COLUMN)
    public String getOwnerId() {
        return ownerId;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute(USER_ACCOUNT_NAME_COLUMN)
    public String getName() {
        return name;
    }

    @DynamoDbAttribute(USER_ACCOUNT_EMAIL_COLUMN)
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute(USER_ACCOUNT_CREDITS_COLUMN)
    public Integer getCredits() {
        return credits;
    }

    @DynamoDbAttribute(USER_ACCOUNT_STATUS_COLUMN)
    public String getStatus() {
        return status;
    }

    @DynamoDbAttribute(USER_ACCOUNT_CREATED_AT_COLUMN)
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static DynamoDBUserAccountEntity fromDomain(UserAccount userAccount) {
        DynamoDBUserAccountEntity entity = new DynamoDBUserAccountEntity();
        entity.setOwnerId(userAccount.getId().value());
        entity.setName(userAccount.getName().value());
        entity.setEmail(userAccount.getEmail().value());
        entity.setCredits(userAccount.creditBalance().value());
        entity.setStatus(userAccount.getStatus().name());

        return entity;
    }
}
