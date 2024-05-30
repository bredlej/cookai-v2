package com.fikafoodie.useraccount.application.services;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountConfigurationPort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountPublicRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

/**
 * This class represents the public service for user accounts.
 * It contains methods for registering and confirming user accounts.
 */
public class UserAccountPublicService {

    private final UserAccountPublicRepositoryPort userAccountPublicRepositoryPort;
    private final UserAccountConfigurationPort userAccountConfigurationPort;

    private final Logger logger = LoggerFactory.getLogger(UserAccountPublicService.class);

    /**
     * Constructs a new UserAccountPublicService with the given repository and configuration ports.
     * @param userAccountPublicRepositoryPort the repository port for public user accounts
     * @param userAccountConfigurationPort the configuration port for user accounts
     */
    public UserAccountPublicService(UserAccountPublicRepositoryPort userAccountPublicRepositoryPort, UserAccountConfigurationPort userAccountConfigurationPort) {
        this.userAccountPublicRepositoryPort = userAccountPublicRepositoryPort;
        this.userAccountConfigurationPort = userAccountConfigurationPort;
    }

    /**
     * Registers a new user account with the given id, name, and email.
     * The new user account is created with 0 credits and a status of NEW.
     * @param id the id of the new user account
     * @param name the name of the new user account
     * @param email the email of the new user account
     */
    public void registerAccount(UserAccount.Id id, UserAccount.Name name, UserAccount.Email email) {
        var newUserAccount = new UserAccount(id, name, email, new UserAccount.Credits(0), UserAccount.Status.NEW);
        userAccountPublicRepositoryPort.createAccount(newUserAccount);
        logger.info("Account created: "+ newUserAccount);
    }

    /**
     * Confirms a user account with the given name.
     * If the user account does not exist or is not in the NEW status, a UserAccountNotFoundException is thrown.
     * Otherwise, the user account's credits are set to the initial credits from the configuration port,
     * and the status is set to ACTIVE.
     * @param name the name of the user account to confirm
     * @throws UserAccountNotFoundException if the user account does not exist or is not in the NEW status
     */
    public void confirmAccount(UserAccount.Name name) throws UserAccountNotFoundException {
        if (userAccountPublicRepositoryPort.getAccountStatus(name).isEmpty()) {
            throw new UserAccountNotFoundException("User account not found");
        }
        if (userAccountPublicRepositoryPort.getAccountStatus(name).get() != UserAccount.Status.NEW) {
            throw new UserAccountNotFoundException("User account not in NEW status");
        }
        userAccountPublicRepositoryPort.setAccountCredits(name, userAccountConfigurationPort.initialCredits());
        userAccountPublicRepositoryPort.setAccountStatus(name, UserAccount.Status.ACTIVE);
    }
}