package com.fikafoodie.recipes.domain.ports.secondary;

import com.fikafoodie.recipes.domain.valueobjects.Picture;

public interface PictureGenerationServicePort {
    Picture generatePicture(String prompt);
}
