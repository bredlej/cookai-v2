package com.fikafoodie.useraccount.infrastructure.adapters.secondary.fake;

import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountPublicRepositoryPort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;

import java.util.Optional;

@InMemory
@ApplicationScoped
public class InMemoryUserAccountRepository implements UserAccountPublicRepositoryPort, UserAccountSecuredRepositoryPort {
    private UserAccount userAccount;

    public InMemoryUserAccountRepository() {
    }

    @Override
    public void createAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public void setAccountCredits(UserAccount.Name name, UserAccount.Credits credits) {
        userAccount.setCredits(credits);
    }

    @Override
    public void setAccountStatus(UserAccount.Name name, UserAccount.Status status) {
        userAccount.setStatus(status);
    }

    @Override
    public Optional<UserAccount.Status> getAccountStatus(UserAccount.Name name) {

        return userAccount == null ? Optional.empty() : Optional.of(userAccount.getStatus());
    }

    @Override
    public UserAccount.Credits getCreditBalance() {
        return userAccount.getCredits();
    }

    @Override
    public void subtractCredits(UserAccount.Credits credits) {
        userAccount.subtractCredits(credits);
    }

    @Override
    public void addCredits(UserAccount.Credits credits) {
        userAccount.addCredits(credits);
    }
}
