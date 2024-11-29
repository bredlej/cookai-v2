package com.fikafoodie.useraccount.domain.ports.primary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.valueobjects.Password;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;

public interface UserAccountPublicServicePort {
    void registerAccount(UserAccount.Name name, UserAccount.Email email, Password password);
    void confirmAccount(UserAccount.Name name) throws UserAccountNotFoundException;
}
