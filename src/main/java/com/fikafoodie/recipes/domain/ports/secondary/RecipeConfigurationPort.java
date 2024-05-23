package com.fikafoodie.recipes.domain.ports.secondary;

import com.fikafoodie.useraccount.domain.entities.UserAccount;

public interface RecipeConfigurationPort {
    UserAccount.Credits getRecipeCreationCost();
}
