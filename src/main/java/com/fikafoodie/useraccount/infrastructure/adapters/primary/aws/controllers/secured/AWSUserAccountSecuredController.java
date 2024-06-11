package com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.controllers.secured;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountSecuredServicePort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
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
       return securedRepositoryPort.getCreditBalance();
    }

    @Override
    public void subtractCredits(UserAccount.Credits credits) throws UserAccountNotFoundException {
        securedRepositoryPort.subtractCredits(credits);

    }
}
