package com.fikafoodie.useraccount.application.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@RegisterForReflection
@Data
public class AuthenticateDTO {
    private String name;
    private String password;
}
