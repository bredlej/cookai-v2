package com.fikafoodie.useraccount.application.services;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.InMemoryUserAccountConfiguration;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.fake.InMemoryUserAccountRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@QuarkusTest
public class UserAccountServiceTest {

    @Test
    void registerAccountShouldCreateAccountInRepositoryWithStatusNew() {
        UserAccountRepositoryPort userAccountRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountService userAccountService = new UserAccountService(
                userAccountRepositoryPort,
                new InMemoryUserAccountConfiguration());

        userAccountService.registerAccount(new UserAccount.Id("id"), new UserAccount.Name("name"), new UserAccount.Email("email"));
        Assertions.assertEquals(UserAccount.Status.NEW, userAccountRepositoryPort.getUserAccount().get().getStatus());
    }

    @Test
    void registerAccountShouldCreateAccountInRepositoryWithZeroCredits() {
        UserAccountRepositoryPort userAccountRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountService userAccountService = new UserAccountService(
                userAccountRepositoryPort,
                new InMemoryUserAccountConfiguration());

        userAccountService.registerAccount(new UserAccount.Id("id"), new UserAccount.Name("name"), new UserAccount.Email("email"));
        Assertions.assertEquals(new UserAccount.Credits(0), userAccountRepositoryPort.getUserAccount().get().creditBalance());
    }

    @Test
    void confirmAccountShouldSetAccountStatusToActive() {
        UserAccountRepositoryPort userAccountRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountService userAccountService = new UserAccountService(
                userAccountRepositoryPort,
                new InMemoryUserAccountConfiguration());

        userAccountService.registerAccount(new UserAccount.Id("id"), new UserAccount.Name("name"), new UserAccount.Email("email"));
        userAccountService.confirmAccount();
        Assertions.assertEquals(UserAccount.Status.ACTIVE, userAccountRepositoryPort.getUserAccount().get().getStatus());
    }

    @Test
    void confirmAccountShouldSetInitialCreditsToAccount() {
        UserAccountRepositoryPort userAccountRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountService userAccountService = new UserAccountService(
                userAccountRepositoryPort,
                () -> new UserAccount.Credits(5)
        );

        userAccountService.registerAccount(new UserAccount.Id("id"), new UserAccount.Name("name"), new UserAccount.Email("email"));
        userAccountService.confirmAccount();
        Assertions.assertEquals(new UserAccount.Credits(5), userAccountRepositoryPort.getUserAccount().get().creditBalance());
    }
}
