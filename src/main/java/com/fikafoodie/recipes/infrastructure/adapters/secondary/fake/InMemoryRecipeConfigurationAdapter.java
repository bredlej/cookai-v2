package com.fikafoodie.recipes.infrastructure.adapters.secondary.fake;

import com.fikafoodie.kernel.qualifiers.InMemory;
import com.fikafoodie.recipes.domain.ports.secondary.RecipeConfigurationPort;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@InMemory
public class InMemoryRecipeConfigurationAdapter implements RecipeConfigurationPort {
    @Override
    public UserAccount.Credits getRecipeCreationCost() {
        return new UserAccount.Credits(1);
    }
}
