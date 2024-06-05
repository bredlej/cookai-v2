package com.fikafoodie.useraccount.application.services;

import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountPublicRepositoryPort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountSecuredRepositoryPort;
import com.fikafoodie.useraccount.application.exceptions.UserAccountNotFoundException;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.fake.InMemoryUserAccountConfiguration;
import com.fikafoodie.useraccount.infrastructure.adapters.secondary.fake.InMemoryUserAccountRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@QuarkusTest
public class UserAccountServiceTest {

    @Test
    void registerAccountShouldCreateAccountInRepositoryWithStatusNew() throws UserAccountNotFoundException {
        UserAccountPublicRepositoryPort userAccountPublicRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountPublicService userAccountPublicService = new UserAccountPublicService(
                userAccountPublicRepositoryPort,
                new InMemoryUserAccountConfiguration());

        userAccountPublicService.registerAccount(new UserAccount.Id("id"), new UserAccount.Name("name"), new UserAccount.Email("email"));
        Assertions.assertEquals(UserAccount.Status.NEW, ((UserAccountSecuredRepositoryPort) userAccountPublicRepositoryPort).getUserAccount().get().getStatus());
    }

    @Test
    void registerAccountShouldCreateAccountInRepositoryWithZeroCredits() throws UserAccountNotFoundException {
        UserAccountPublicRepositoryPort userAccountPublicRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountPublicService userAccountPublicService = new UserAccountPublicService(
                userAccountPublicRepositoryPort,
                new InMemoryUserAccountConfiguration());

        userAccountPublicService.registerAccount(new UserAccount.Id("id"), new UserAccount.Name("name"), new UserAccount.Email("email"));
        Assertions.assertEquals(new UserAccount.Credits(0), ((UserAccountSecuredRepositoryPort) userAccountPublicRepositoryPort).getUserAccount().get().creditBalance());
    }

    @Test
    void confirmAccountShouldSetAccountStatusToActive() throws UserAccountNotFoundException {
        UserAccountPublicRepositoryPort userAccountPublicRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountPublicService userAccountPublicService = new UserAccountPublicService(
                userAccountPublicRepositoryPort,
                new InMemoryUserAccountConfiguration());

        userAccountPublicService.registerAccount(new UserAccount.Id("id"), new UserAccount.Name("name"), new UserAccount.Email("email"));
        userAccountPublicService.confirmAccount(new UserAccount.Name("name"));
        Assertions.assertEquals(UserAccount.Status.ACTIVE, ((UserAccountSecuredRepositoryPort) userAccountPublicRepositoryPort).getUserAccount().get().getStatus());
    }

    @Test
    void confirmAccountShouldSetInitialCreditsToAccount() throws UserAccountNotFoundException {
        UserAccountPublicRepositoryPort userAccountPublicRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountPublicService userAccountPublicService = new UserAccountPublicService(
                userAccountPublicRepositoryPort,
                () -> new UserAccount.Credits(5)
        );

        userAccountPublicService.registerAccount(new UserAccount.Id("id"), new UserAccount.Name("name"), new UserAccount.Email("email"));
        userAccountPublicService.confirmAccount(new UserAccount.Name("name"));
        Assertions.assertEquals(new UserAccount.Credits(5), ((UserAccountSecuredRepositoryPort) userAccountPublicRepositoryPort).getUserAccount().get().creditBalance());
    }

    @Test
    void confirmAccountShouldThrowExceptionWhenAccountNotFound() {
        UserAccountPublicRepositoryPort userAccountPublicRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountPublicService userAccountPublicService = new UserAccountPublicService(
                userAccountPublicRepositoryPort,
                new InMemoryUserAccountConfiguration());

        Assertions.assertThrows(UserAccountNotFoundException.class, () -> userAccountPublicService.confirmAccount(new UserAccount.Name("name")));
    }

    @Test
    void confirmAccountShouldThrowExceptionWhenAccountNotInNewStatus() throws UserAccountNotFoundException {
        UserAccountPublicRepositoryPort userAccountPublicRepositoryPort = new InMemoryUserAccountRepository();
        UserAccountPublicService userAccountPublicService = new UserAccountPublicService(
                userAccountPublicRepositoryPort,
                new InMemoryUserAccountConfiguration());

        userAccountPublicService.registerAccount(new UserAccount.Id("id"), new UserAccount.Name("name"), new UserAccount.Email("email"));
        userAccountPublicService.confirmAccount(new UserAccount.Name("name"));
        Assertions.assertThrows(UserAccountNotFoundException.class, () -> userAccountPublicService.confirmAccount(new UserAccount.Name("name")));
    }
}
