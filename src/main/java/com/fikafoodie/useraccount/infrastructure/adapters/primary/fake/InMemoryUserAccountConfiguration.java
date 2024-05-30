package com.fikafoodie.useraccount.infrastructure.adapters.primary.fake;

import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountConfigurationPort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@InMemory
public class InMemoryUserAccountConfiguration implements UserAccountConfigurationPort {

    @Override
    public UserAccount.Credits initialCredits() {
        return new UserAccount.Credits(10);
    }
}
