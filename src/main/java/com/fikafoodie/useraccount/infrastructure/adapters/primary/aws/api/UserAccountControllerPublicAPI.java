package com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.api;

import com.fikafoodie.useraccount.application.dto.AuthenticateDTO;
import com.fikafoodie.useraccount.application.dto.ConfirmSignUpDTO;
import com.fikafoodie.useraccount.application.dto.SignUpDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("v1/user")
public interface UserAccountControllerPublicAPI {
    String COGNITO_USERNAME_CLAIM = "cognito:username";

    @POST
    @Path("/sign-up")
    Response signUpRequest(SignUpDTO signUpDTO);

    @POST
    @Path("/confirm-sign-up")
    Response confirmSignUpRequest(ConfirmSignUpDTO confirmSignUpDTO);

    @POST
    @Path("/authenticate")
    Response authenticateRequest(AuthenticateDTO authenticateDTO);
}