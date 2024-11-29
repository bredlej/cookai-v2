package com.fikafoodie.recipes.domain.ports.secondary;

import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.valueobjects.Picture;
import com.fikafoodie.useraccount.domain.entities.UserAccount;

public interface PictureStorageServicePort {
    Recipe.Picture storePicture(Picture picture, UserAccount.Name userName, Recipe.Id recipeId);
}
