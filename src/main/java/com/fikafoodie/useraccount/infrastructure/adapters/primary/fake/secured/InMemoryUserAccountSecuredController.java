package com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.secured;

import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
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
        if (userAccountSecuredRepositoryPort.getUserAccount().isEmpty()) {
            throw new UserAccountNotFoundException("User account not found");
        }
        return userAccountSecuredRepositoryPort.getUserAccount().get().creditBalance();
    }

    @Override
    public void subtractCredits(UserAccount.Credits credits) throws UserAccountNotFoundException {
        if (userAccountSecuredRepositoryPort.getUserAccount().isEmpty()) {
            throw new UserAccountNotFoundException("User account not found");
        }
        userAccountSecuredRepositoryPort.getUserAccount().get().subtractCredits(credits);
    }
}
