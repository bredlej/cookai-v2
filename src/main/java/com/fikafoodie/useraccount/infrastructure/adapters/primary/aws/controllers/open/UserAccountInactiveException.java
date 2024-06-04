package com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.controllers.open;

public class UserAccountInactiveException extends Throwable {
    public UserAccountInactiveException(String userAccountNotActive) {
        super(userAccountNotActive);
    }
}
