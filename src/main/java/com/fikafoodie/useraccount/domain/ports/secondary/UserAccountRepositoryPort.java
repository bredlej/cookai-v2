package com.fikafoodie.useraccount.domain.ports.secondary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;

public interface UserAccountRepositoryPort {
    UserAccount getUserAccount();
}
