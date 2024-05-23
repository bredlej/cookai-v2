package com.fikafoodie.useraccount.infrastructure.adapters.primary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountServicePort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.InMemoryUserAccountRepository;

public class InMemoryUserAccountController implements UserAccountServicePort {
    private final UserAccountRepositoryPort userAccountRepositoryPort;

    public InMemoryUserAccountController(UserAccount.Credits initialCredits) {
        this.userAccountRepositoryPort = new InMemoryUserAccountRepository();
        this.userAccountRepositoryPort.getUserAccount().addCredits(initialCredits);
    }

    @Override
    public UserAccount.Credits getCreditBalance() {
        return userAccountRepositoryPort.getUserAccount().creditBalance();
    }

    @Override
    public void subtractCredits(UserAccount.Credits credits) {
        userAccountRepositoryPort.getUserAccount().subtractCredits(credits);
    }
}
