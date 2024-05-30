package com.fikafoodie.useraccount.infrastructure.adapters.primary.aws;

import com.fikafoodie.kernel.qualifiers.DynamoDB;
import com.fikafoodie.useraccount.application.dto.AuthenticateDTO;
import com.fikafoodie.useraccount.application.dto.ConfirmSignUpDTO;
import com.fikafoodie.useraccount.application.dto.SignUpDTO;
import com.fikafoodie.useraccount.application.services.UserAccountService;
import com.fikafoodie.useraccount.domain.entities.UserAccount;
import com.fikafoodie.useraccount.domain.valueobjects.ConfirmationCode;
import com.fikafoodie.useraccount.domain.valueobjects.Password;
import com.fikafoodie.useraccount.domain.ports.primary.UserAccountServicePort;
import com.fikafoodie.useraccount.domain.ports.secondary.UserAccountRepositoryPort;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.api.UserAccountControllerCognitoAPI;
import com.fikafoodie.useraccount.infrastructure.adapters.primary.aws.dto.AuthenticateResponseDTO;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@ApplicationScoped
public class AWSUserAccountController implements UserAccountServicePort, UserAccountControllerCognitoAPI {
    private final Logger logger = LoggerFactory.getLogger(AWSUserAccountController.class);

    private final UserAccountRepositoryPort userAccountRepositoryPort;

    private final UserAccountService userAccountService;

    @ConfigProperty(name = "aws.cognito.clientId")
    String clientId;

    @Inject
    @ConfigProperty(name = "aws.cognito.userPoolId")
    String userPoolId;

    @Inject
    public AWSUserAccountController(@DynamoDB UserAccountRepositoryPort userAccountRepositoryPort) {
        this.userAccountRepositoryPort = userAccountRepositoryPort;
        userAccountService = new UserAccountService(userAccountRepositoryPort, () -> new UserAccount.Credits(10));
    }

    @Override
    public Response signUpRequest(SignUpDTO signUpDTO) {
        registerAccount(
                new UserAccount.Name(signUpDTO.getName()),
                new UserAccount.Email(signUpDTO.getEmail()),
                new Password(signUpDTO.getPassword()));
        return Response.ok().build();
    }

    @Override
    public Response confirmSignUpRequest(ConfirmSignUpDTO confirmSignUpDTO) {
        confirmSignUp(
                new UserAccount.Name(confirmSignUpDTO.getName()),
                new ConfirmationCode(confirmSignUpDTO.getConfirmationCode()));
        return Response.ok().build();
    }

    @Override
    public AuthenticateResponseDTO authenticateRequest(AuthenticateDTO authenticateDTO) {
        return authenticate(
                new UserAccount.Name(authenticateDTO.getName()),
                new Password(authenticateDTO.getPassword()));
    }

    private AuthenticateResponseDTO authenticate(UserAccount.Name name, Password password) {
        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .clientId(clientId)
                .userPoolId(userPoolId)
                .authParameters(
                        new java.util.HashMap<>() {{
                            put("USERNAME", name.value());
                            put("PASSWORD", password.toString());
                        }}
                ).build();

        AuthenticateResponseDTO responseDTO = new AuthenticateResponseDTO();

        try (CognitoIdentityProviderClient cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder().region(Region.EU_WEST_1).build()) {
            AdminInitiateAuthResponse result = cognitoIdentityProviderClient.adminInitiateAuth(authRequest);
            logger.info("Authenticate result: " + result);

            responseDTO.setAccessToken(result.authenticationResult().accessToken());
            responseDTO.setIdToken(result.authenticationResult().idToken());
            responseDTO.setRefreshToken(result.authenticationResult().refreshToken());
            responseDTO.setExpiresIn(result.authenticationResult().expiresIn());
            responseDTO.setTokenType(result.authenticationResult().tokenType());
        } catch (Exception e) {
            logger.error("Error authenticating user", e);
        }

        return responseDTO;
    }

    public void confirmSignUp(UserAccount.Name name, ConfirmationCode confirmationCode) {
        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .clientId(clientId)
                .username(name.value())
                .confirmationCode(confirmationCode.value()).build();
        AdminAddUserToGroupRequest adminAddUserToGroupRequest = AdminAddUserToGroupRequest.builder()
                .groupName("user")
                .username(name.value())
                .userPoolId(userPoolId).build();

        try (CognitoIdentityProviderClient cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder().region(Region.EU_WEST_1).build()) {
            ConfirmSignUpResponse result = cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequest);
            logger.info("Sign up result: " + result);
            AdminAddUserToGroupResponse addUserToGroupResponse = cognitoIdentityProviderClient.adminAddUserToGroup(adminAddUserToGroupRequest);
            logger.info("Add user to group result: " + addUserToGroupResponse);

            userAccountService.confirmAccount();
        }
    }

    @Override
    public void registerAccount(UserAccount.Name name, UserAccount.Email email, Password password) {
        SignUpRequest request = SignUpRequest.builder()
                .clientId(clientId)
                .username(name.value())
                .password(password.toString())
                .userAttributes(
                        AttributeType.builder()
                                .name("email")
                                .value(email.value()).build()
                ).build();

        try (CognitoIdentityProviderClient cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder().region(Region.EU_WEST_1).build()) {
            SignUpResponse result = cognitoIdentityProviderClient.signUp(request);
            logger.info("Sign up result: " + result);

            userAccountService.registerAccount(new UserAccount.Id(result.userSub()), name, email);
        }
    }

    @Override
    public void confirmAccount() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public UserAccount.Credits getCreditBalance() throws UserAccountNotFoundException {
        if (userAccountRepositoryPort.getUserAccount().isEmpty()) {
            throw new UserAccountNotFoundException("User account not found");
        }
        else {
            return userAccountRepositoryPort.getUserAccount().get().creditBalance();
        }
    }

    @Override
    public void subtractCredits(UserAccount.Credits credits) throws UserAccountNotFoundException {
        if (userAccountRepositoryPort.getUserAccount().isEmpty()) {
            throw new UserAccountNotFoundException("User account not found");
        }
        else {
            userAccountRepositoryPort.getUserAccount().get().subtractCredits(credits);
        }
    }
}
