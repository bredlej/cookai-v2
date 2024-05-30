package com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.dto;

import lombok.Data;

@Data
public class AuthenticateResponseDTO {
    String accessToken;
    int expiresIn;
    String tokenType;
    String refreshToken;
    String idToken;
}
