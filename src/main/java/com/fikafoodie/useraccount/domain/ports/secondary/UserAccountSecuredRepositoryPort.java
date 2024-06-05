package com.fikafoodie.useraccount.domain.ports.secondary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import io.quarkus.security.Authenticated;

import java.util.Optional;

@Authenticated
public interface UserAccountSecuredRepositoryPort {
    Optional<UserAccount> getUserAccount() throws UserAccountNotFoundException;
}
