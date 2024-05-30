package com.fikafoodie.useraccount.application.services;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountConfigurationPort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountRepositoryPort;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class UserAccountService {

    private final UserAccountRepositoryPort userAccountRepositoryPort;
    private final UserAccountConfigurationPort userAccountConfigurationPort;

    private final Logger logger = LoggerFactory.getLogger(UserAccountService.class);

    public UserAccountService(UserAccountRepositoryPort userAccountRepositoryPort, UserAccountConfigurationPort userAccountConfigurationPort) {
        this.userAccountRepositoryPort = userAccountRepositoryPort;
        this.userAccountConfigurationPort = userAccountConfigurationPort;
    }

    public void registerAccount(UserAccount.Id id, UserAccount.Name name, UserAccount.Email email) {
        var newUserAccount = new UserAccount(id, name, email, new UserAccount.Credits(0), UserAccount.Status.NEW);
        userAccountRepositoryPort.createAccount(newUserAccount);
        logger.info("Account created: "+ newUserAccount);
    }

    public void confirmAccount() {
        userAccountRepositoryPort.setAccountCredits(userAccountConfigurationPort.initialCredits());
        userAccountRepositoryPort.setAccountStatus(UserAccount.Status.ACTIVE);
    }
}
