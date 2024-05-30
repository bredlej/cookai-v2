package com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.api;

import com.fikafoodie.useraccount.application.dto.AuthenticateDTO;
import com.fikafoodie.useraccount.application.dto.ConfirmSignUpDTO;
import com.fikafoodie.useraccount.application.dto.SignUpDTO;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.dto.AuthenticateResponseDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("v1/user")
public interface UserAccountControllerCognitoAPI {
    @POST
    @Path("/sign-up")
    Response signUpRequest(SignUpDTO signUpDTO);

    @Path("/confirm-sign-up")
    Response confirmSignUpRequest(ConfirmSignUpDTO confirmSignUpDTO);

    @POST
    @Path("/authenticate")
    AuthenticateResponseDTO authenticateRequest(AuthenticateDTO authenticateDTO);
}