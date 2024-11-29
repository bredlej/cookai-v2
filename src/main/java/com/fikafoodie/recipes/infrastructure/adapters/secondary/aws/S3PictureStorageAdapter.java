package com.fikafoodie.recipes.infrastructure.adapters.secondary.aws;

import com.fikafoodie.kernel.qualifiers.S3;
import com.fikafoodie.recipes.domain.entities.Recipe;
import com.fikafoodie.recipes.domain.ports.secondary.PictureStorageServicePort;
import com.fikafoodie.recipes.domain.valueobjects.Picture;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Base64;

@ApplicationScoped
@S3
public class S3PictureStorageAdapter implements PictureStorageServicePort {

    private final S3Client s3Client;
    private final String bucketName;

    @Inject
    public S3PictureStorageAdapter(@ConfigProperty(name = "aws.region") String s3Region, S3Client s3Client, @ConfigProperty(name = "bucket.name") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public Recipe.Picture storePicture(Picture picture, UserAccount.Name userName, Recipe.Id recipeId) {
        byte[] decodedPictureBytes = Base64.getDecoder().decode(picture.data());
        s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key("recipes/" + recipeId.value() + ".webp").build(), RequestBody.fromByteBuffer(java.nio.ByteBuffer.wrap(decodedPictureBytes)));
        return new Recipe.Picture("https://"+bucketName+".s3.eu-west-1.amazonaws.com/recipes/"+recipeId.value()+".webp");
    }
}
