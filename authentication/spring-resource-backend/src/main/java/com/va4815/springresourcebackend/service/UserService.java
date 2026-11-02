package com.va4815.springresourcebackend.service;

import com.va4815.springresourcebackend.dto.request.UserLoginRequestDTO;
import com.va4815.springresourcebackend.dto.request.UserRegisterRequestDTO;
import com.va4815.springresourcebackend.dto.response.TokenResponseDTO;
import com.va4815.springresourcebackend.exception.GlobalArgumentException;
import com.va4815.springresourcebackend.repository.UserRepository;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Value("${KC_SERVER_URL}")
    private String kcServerUrl;

    @Value("${KC_REALM}")
    private String kcRealm;

    @Value("${KC_CLIENT_ID}")
    private String kcClientId;

    @Value("${KC_CLIENT_SECRET}")
    private String kcClientSecret;


    private final UserRepository userRepository;
    private final Keycloak keycloakAdminClient;

    public UserService(UserRepository userRepository, Keycloak keycloakAdminClient) {
        this.userRepository = userRepository;
        this.keycloakAdminClient = keycloakAdminClient;
    }

    public TokenResponseDTO login(UserLoginRequestDTO requestDTO) {

        // TODO: encryt username password
        String username = requestDTO.getUsername();
        String password = requestDTO.getPassword();

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(kcServerUrl)
                .realm(kcRealm)
                .clientId(kcClientId)
                .clientSecret(kcClientSecret)
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD)
                .build();

        if (keycloak == null) {
            throw new RuntimeException("Failed to login");
        }

        AccessTokenResponse accessTokenResponse = keycloak.tokenManager().grantToken();

        if (accessTokenResponse == null) {
            throw new RuntimeException("Failed to retrieve access token");
        }

        return new TokenResponseDTO(accessTokenResponse);
    }

    public void register(UserRegisterRequestDTO requestDTO) {
        if (!requestDTO.getPassword().equals(requestDTO.getConfirmPassword())) {
            throw new GlobalArgumentException(HttpStatus.BAD_REQUEST, "Password confirmation does not match");
        }

        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(requestDTO.getPassword());
        passwordCredential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setEmailVerified(false);
        user.setEnabled(true);
        user.setCredentials(List.of(passwordCredential));

        try (Response response = keycloakAdminClient.realm(kcRealm).users().create(user)) {
            switch (response.getStatus()) {
                case 201 -> {
                    return;
                }
                case 400 -> throw new GlobalArgumentException(
                        HttpStatus.BAD_REQUEST,
                        "Registration details do not satisfy the identity provider requirements"
                );
                case 409 -> throw new GlobalArgumentException(
                        HttpStatus.CONFLICT,
                        "An account with the supplied username or email already exists"
                );
                default -> {
                    LOGGER.error("Keycloak user creation failed with HTTP status {}", response.getStatus());
                    throw new GlobalArgumentException(
                            HttpStatus.BAD_GATEWAY,
                            "The identity provider rejected the registration request"
                    );
                }
            }
        } catch (ProcessingException ex) {
            LOGGER.error("Keycloak is unavailable while creating a user", ex);
            throw new GlobalArgumentException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "The identity provider is temporarily unavailable"
            );
        } catch (WebApplicationException ex) {
            int status = ex.getResponse() == null ? 0 : ex.getResponse().getStatus();
            LOGGER.error("Keycloak user creation request failed with HTTP status {}", status, ex);
            throw new GlobalArgumentException(
                    HttpStatus.BAD_GATEWAY,
                    "The identity provider rejected the registration request"
            );
        }

    }

}
