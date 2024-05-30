package com.fikafoodie.useraccount.domain.ports.secondary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;

import java.util.Optional;

public interface UserAccountRepositoryPort {
    void createAccount(UserAccount userAccount);
    Optional<UserAccount> getUserAccount();
    void setAccountCredits(UserAccount.Credits credits);
    void setAccountStatus(UserAccount.Status status);
}
