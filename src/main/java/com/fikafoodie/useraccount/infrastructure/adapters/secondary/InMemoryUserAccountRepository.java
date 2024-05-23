package com.fikafoodie.useraccount.infrastructure.adapters.secondary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountRepositoryPort;

public class InMemoryUserAccountRepository implements UserAccountRepositoryPort {
    private final UserAccount userAccount;

    public InMemoryUserAccountRepository() {
        userAccount = new UserAccount();
    }

    @Override
    public UserAccount getUserAccount() {
        return userAccount;
    }
}
