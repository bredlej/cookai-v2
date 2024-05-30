package com.fikafoodie.useraccount.domain.ports.primary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import io.quarkus.security.Authenticated;

@Authenticated
public interface UserAccountSecuredServicePort {
    UserAccount.Credits getCreditBalance() throws UserAccountNotFoundException;
    void subtractCredits(UserAccount.Credits credits) throws UserAccountNotFoundException;
}
