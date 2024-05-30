package com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.controllers.secured;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AWSUserAccountSecuredController implements UserAccountSecuredServicePort {
    private final UserAccountSecuredRepositoryPort securedRepositoryPort;

    @Inject
    public AWSUserAccountSecuredController(@DynamoDB UserAccountSecuredRepositoryPort securedRepositoryPort) {
        this.securedRepositoryPort = securedRepositoryPort;
    }

    @Override
    public UserAccount.Credits getCreditBalance() throws UserAccountNotFoundException {
        if (securedRepositoryPort.getUserAccount().isEmpty()) {
            throw new UserAccountNotFoundException("User account not found");
        }
        else {
            return securedRepositoryPort.getUserAccount().get().creditBalance();
        }
    }

    @Override
    public void subtractCredits(UserAccount.Credits credits) throws UserAccountNotFoundException {
        if (securedRepositoryPort.getUserAccount().isEmpty()) {
            throw new UserAccountNotFoundException("User account not found");
        }
        else {
            securedRepositoryPort.getUserAccount().get().subtractCredits(credits);
        }
    }
}
