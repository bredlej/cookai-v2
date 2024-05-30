package com.fikafoodie.useraccount.domain.ports.secondary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;

import java.util.Optional;

public interface UserAccountPublicRepositoryPort {
    void createAccount(UserAccount userAccount);
    void setAccountCredits(UserAccount.Name name, UserAccount.Credits credits);
    void setAccountStatus(UserAccount.Name name, UserAccount.Status status);
    Optional<UserAccount.Status> getAccountStatus(UserAccount.Name name);
}
