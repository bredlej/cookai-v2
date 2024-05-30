package com.fikafoodie.useraccount.infrastructure.adapters.primary.fake;

import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.useraccount.application.services.UserAccountService;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountServicePort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountConfigurationPort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountRepositoryPort;
import com.fikafoodie.useraccount.domain.valueobjects.Password;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@ApplicationScoped
@InMemory
public class InMemoryUserAccountController implements UserAccountServicePort {
    private final UserAccountRepositoryPort userAccountRepositoryPort;
    private final UserAccountService userAccountService;

    @Inject
    public InMemoryUserAccountController(@InMemory UserAccountRepositoryPort userAccountRepository, @InMemory UserAccountConfigurationPort userAccountConfiguration) {
        this.userAccountRepositoryPort = userAccountRepository;
        this.userAccountService = new UserAccountService(userAccountRepositoryPort, userAccountConfiguration);
    }

    @Override
    public void registerAccount(UserAccount.Name name, UserAccount.Email email, Password password) {
        userAccountService.registerAccount(new UserAccount.Id(UUID.randomUUID().toString()), name, email);
    }

    @Override
    public void confirmAccount() {
        userAccountService.confirmAccount();
    }

    @Override
    public UserAccount.Credits getCreditBalance() {
        return userAccountRepositoryPort.getUserAccount().get().creditBalance();
    }

    @Override
    public void subtractCredits(UserAccount.Credits credits) {
        userAccountRepositoryPort.getUserAccount().get().subtractCredits(credits);
    }
}

