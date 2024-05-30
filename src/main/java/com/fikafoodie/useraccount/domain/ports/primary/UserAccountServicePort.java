package com.fikafoodie.useraccount.domain.ports.primary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.valueobjects.Password;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;

public interface UserAccountServicePort {
    void registerAccount(UserAccount.Name name, UserAccount.Email email, Password password);
    void confirmAccount();
    UserAccount.Credits getCreditBalance() throws UserAccountNotFoundException;
    void subtractCredits(UserAccount.Credits credits) throws UserAccountNotFoundException;
}
