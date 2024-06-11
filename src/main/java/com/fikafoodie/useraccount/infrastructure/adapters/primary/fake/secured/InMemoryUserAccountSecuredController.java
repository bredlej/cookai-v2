package com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.secured;

import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
@InMemory
public class InMemoryUserAccountSecuredController implements UserAccountSecuredServicePort {

    private final UserAccountSecuredRepositoryPort userAccountSecuredRepositoryPort;

    @Inject
    public InMemoryUserAccountSecuredController(@InMemory UserAccountSecuredRepositoryPort userAccountSecuredRepositoryPort) {
        this.userAccountSecuredRepositoryPort = userAccountSecuredRepositoryPort;
    }

    @Override
    public UserAccount.Credits getCreditBalance() throws UserAccountNotFoundException {
        return userAccountSecuredRepositoryPort.getCreditBalance();
    }

    @Override
    public void subtractCredits(UserAccount.Credits credits) throws UserAccountNotFoundException {
        userAccountSecuredRepositoryPort.subtractCredits(credits);
    }
}
