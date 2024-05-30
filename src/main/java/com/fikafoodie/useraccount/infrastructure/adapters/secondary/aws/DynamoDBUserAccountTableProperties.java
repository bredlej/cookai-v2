package com.fikafoodie.useraccount.infrastructure.adapters.secondary.aws;

public interface DynamoDBUserAccountTableProperties {
    String USER_ACCOUNT_OWNERID_COLUMN = "ownerId";
    String USER_ACCOUNT_NAME_COLUMN = "name";
    String USER_ACCOUNT_EMAIL_COLUMN = "email";
    String USER_ACCOUNT_CREDITS_COLUMN = "credits";
    String USER_ACCOUNT_STATUS_COLUMN = "status";
}
