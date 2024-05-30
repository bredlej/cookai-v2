package com.fikafoodie.useraccount.infrastructure.adapters.secondary.fake;

import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@InMemory
@ApplicationScoped
public class InMemoryUserAccountRepository implements UserAccountRepositoryPort {
    private UserAccount userAccount;

    public InMemoryUserAccountRepository() {
    }

    @Override
    public void createAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public Optional<UserAccount> getUserAccount() {
        return Optional.ofNullable(userAccount);
    }

    @Override
    public void setAccountCredits(UserAccount.Credits credits) {
        userAccount.setCredits(credits);
    }

    @Override
    public void setAccountStatus(UserAccount.Status status) {
        userAccount.setStatus(status);
    }
}
