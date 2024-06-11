package com.fikafoodie.useraccount.domain.ports.secondary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import io.quarkus.security.Authenticated;

public interface UserAccountSecuredRepositoryPort {
    @Authenticated
    UserAccount.Credits getCreditBalance() throws UserAccountNotFoundException;
    @Authenticated
    void subtractCredits(UserAccount.Credits credits) throws UserAccountNotFoundException;
    @Authenticated
    void addCredits(UserAccount.Credits credits) throws UserAccountNotFoundException;
}
