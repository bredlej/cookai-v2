package com.fikafoodie.useraccount.application.exceptions;

public class UserAccountNotFoundException extends Throwable {
    public UserAccountNotFoundException(String message) {
        super(message);
    }
}
