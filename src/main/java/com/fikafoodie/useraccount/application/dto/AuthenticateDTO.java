package com.fikafoodie.useraccount.application.dto;

import lombok.Data;

@Data
public class AuthenticateDTO {
    private String name;
    private String password;
}
