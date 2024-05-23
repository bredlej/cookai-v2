package com.fikafoodie.useraccount.domain.ports.primary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;

public interface UserAccountServicePort {
    UserAccount.Credits getCreditBalance();
    void subtractCredits(UserAccount.Credits credits);
}
