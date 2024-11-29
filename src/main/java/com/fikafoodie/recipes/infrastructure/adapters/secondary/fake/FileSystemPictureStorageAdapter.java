package com.fikafoodie.recipes.infrastructure.adapters.secondary.fake;

import com.fikafoodie.kernel.qualifiers.FileSystem;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.PictureStorageServicePort;
import com.fikafoodie.recipes.domain.valueobjects.Picture;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@ApplicationScoped
@FileSystem
public class FileSystemPictureStorageAdapter implements PictureStorageServicePort {

    @Override
    public Recipe.Picture storePicture(Picture picture, UserAccount.Name userName, Recipe.Id recipeId) {
        // save picture base64 data to file system as binary
        byte[] decodedPictureBytes = Base64.getDecoder().decode(picture.data());
        String encodedPictureName = Base64.getEncoder().encodeToString((userName+"-"+recipeId.value()).getBytes());

        Path path = Paths.get( "/tmp/fikafoodie/pictures/" + encodedPictureName+ ".webp");
        try {
            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.write(path, decodedPictureBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Recipe.Picture(encodedPictureName + ".webp");
    }
}
