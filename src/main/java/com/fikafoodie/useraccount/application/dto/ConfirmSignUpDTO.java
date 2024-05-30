package com.fikafoodie.useraccount.application.dto;

import lombok.Data;

@Data
public class ConfirmSignUpDTO {
    private String name;
    private String confirmationCode;
}
