package com.fikafoodie.useraccount.application.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

@RegisterForReflection
@Data
public class SignUpDTO {
    private String name;
    private String email;
    private String password;
}
