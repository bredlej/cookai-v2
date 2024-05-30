package com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.open;

import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.useraccount.application.services.UserAccountPublicService;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountPublicServicePort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountConfigurationPort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountPublicRepositoryPort;
import com.fikafoodie.useraccount.domain.valueobjects.Password;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.UserAccountNotFoundException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@RequestScoped
@InMemory
public class InMemoryUserAccountPublicController implements UserAccountPublicServicePort {
    private final UserAccountPublicService userAccountPublicService;

    @Inject
    public InMemoryUserAccountPublicController(@InMemory UserAccountPublicRepositoryPort userAccountPublicRepositoryPort, @InMemory UserAccountConfigurationPort userAccountConfiguration) {
        this.userAccountPublicService = new UserAccountPublicService(userAccountPublicRepositoryPort, userAccountConfiguration);
    }

    @Override
    public void registerAccount(UserAccount.Name name, UserAccount.Email email, Password password) {
        userAccountPublicService.registerAccount(new UserAccount.Id(UUID.randomUUID().toString()), name, email);
    }

    @Override
    public void confirmAccount(UserAccount.Name name) throws UserAccountNotFoundException {
        userAccountPublicService.confirmAccount(name);
    }


}

