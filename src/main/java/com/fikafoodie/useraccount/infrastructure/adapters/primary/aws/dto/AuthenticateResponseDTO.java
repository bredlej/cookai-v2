package com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@RegisterForReflection
@Data
public class AuthenticateResponseDTO {
    String accessToken;
    int expiresIn;
    String tokenType;
    String refreshToken;
    String idToken;
}
