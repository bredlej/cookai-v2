package com.fikafoodie.useraccount.infrastructure.adapters.primary.aws;

public class UserAccountNotFoundException extends Throwable {
    public UserAccountNotFoundException(String message) {
        super(message);
    }
}
