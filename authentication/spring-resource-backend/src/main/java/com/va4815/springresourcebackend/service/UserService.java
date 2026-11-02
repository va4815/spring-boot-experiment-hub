package com.va4815.springresourcebackend.service;

import com.va4815.springresourcebackend.dto.request.UserLoginRequestDTO;
import com.va4815.springresourcebackend.dto.request.UserRegisterRequestDTO;
import com.va4815.springresourcebackend.dto.response.TokenResponseDTO;
import com.va4815.springresourcebackend.dto.response.UserResponseDTO;
import com.va4815.springresourcebackend.entity.User;
import com.va4815.springresourcebackend.exception.GlobalArgumentException;
import com.va4815.springresourcebackend.model.CurrentUser;
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
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private static final Logger Logger = LoggerFactory.getLogger(UserService.class);

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
    private final CurrentUser currentUser;

    public UserService(UserRepository userRepository, Keycloak keycloakAdminClient, CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.keycloakAdminClient = keycloakAdminClient;
        this.currentUser = currentUser;
    }

    public TokenResponseDTO login(UserLoginRequestDTO requestDTO) {

        // TODO: encrypt username password
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

    @Transactional
    public UserResponseDTO register(UserRegisterRequestDTO requestDTO) {
        if (!requestDTO.getPassword().equals(requestDTO.getConfirmPassword())) {
            throw new GlobalArgumentException(HttpStatus.BAD_REQUEST, "Password confirmation does not match");
        }

        String keycloakUserId = createKeycloakUser(requestDTO);

        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setKcId(keycloakUserId);
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setCreatedBy(currentUser.getUsername());
        user.setUpdatedBy(currentUser.getUsername());

        try {
            User savedUser = userRepository.saveAndFlush(user);
            return UserResponseDTO.fromUser(savedUser);
        } catch (DataAccessException ex) {
            Logger.error("Database user creation failed for Keycloak user {}", keycloakUserId, ex);
            // TODO: Call the /users/delete API with keycloakUserId to compensate for the Keycloak account creation.
            throw ex;
        }
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new GlobalArgumentException(
                HttpStatus.NOT_FOUND,
                "User not found"
        ));

        String keycloakUserId = user.getKcId();
        if (keycloakUserId == null || keycloakUserId.isBlank()) {
            Logger.error("Database user {} does not have a Keycloak user identifier", id);
            throw new GlobalArgumentException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "The user account cannot be deleted because its identity record is invalid"
            );
        }

        deleteKeycloakUser(keycloakUserId);

        try {
            userRepository.delete(user);
            userRepository.flush();
        } catch (DataAccessException ex) {
            Logger.error(
                    "Database user {} could not be deleted after Keycloak user {} was deleted",
                    id,
                    keycloakUserId,
                    ex
            );
            throw ex;
        }
    }

    private void deleteKeycloakUser(String keycloakUserId) {
        try {
            keycloakAdminClient.realm(kcRealm).users().get(keycloakUserId).remove();
        } catch (ProcessingException ex) {
            Logger.error("Keycloak is unavailable while deleting user {}", keycloakUserId, ex);
            throw new GlobalArgumentException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "The identity provider is temporarily unavailable"
            );
        } catch (WebApplicationException ex) {
            int status = ex.getResponse() == null ? 0 : ex.getResponse().getStatus();
            if (status == HttpStatus.NOT_FOUND.value()) {
                Logger.warn(
                        "Keycloak user {} was already absent; continuing local user deletion",
                        keycloakUserId
                );
                return;
            }

            Logger.error(
                    "Keycloak user deletion request for user {} failed with HTTP status {}",
                    keycloakUserId,
                    status,
                    ex
            );
            throw new GlobalArgumentException(
                    HttpStatus.BAD_GATEWAY,
                    "The identity provider rejected the user deletion request"
            );
        }
    }

    private String createKeycloakUser(UserRegisterRequestDTO requestDTO) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setEmailVerified(false);
        user.setEnabled(true);

        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(requestDTO.getPassword());
        passwordCredential.setTemporary(false);
        user.setCredentials(List.of(passwordCredential));

        try (Response response = keycloakAdminClient.realm(kcRealm).users().create(user)) {
            switch (response.getStatus()) {
                case 201 -> {
                    return getCreatedKeycloakUserId(response);
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
                    Logger.error("Keycloak user creation failed with HTTP status {}", response.getStatus());
                    throw new GlobalArgumentException(
                            HttpStatus.BAD_GATEWAY,
                            "The identity provider rejected the registration request"
                    );
                }
            }
        } catch (ProcessingException ex) {
            Logger.error("Keycloak is unavailable while creating a user", ex);
            throw new GlobalArgumentException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "The identity provider is temporarily unavailable"
            );
        } catch (WebApplicationException ex) {
            int status = ex.getResponse() == null ? 0 : ex.getResponse().getStatus();
            Logger.error("Keycloak user creation request failed with HTTP status {}", status, ex);
            throw new GlobalArgumentException(
                    HttpStatus.BAD_GATEWAY,
                    "The identity provider rejected the registration request"
            );
        }
    }

    private String getCreatedKeycloakUserId(Response response) {
        URI location = response.getLocation();
        if (location == null || location.getPath() == null) {
            Logger.error("Keycloak user creation response did not include a Location header");
            throw new GlobalArgumentException(
                    HttpStatus.BAD_GATEWAY,
                    "The identity provider returned an invalid registration response"
            );
        }

        String path = location.getPath();
        String keycloakUserId = path.substring(path.lastIndexOf('/') + 1);
        if (keycloakUserId.isBlank()) {
            Logger.error("Keycloak user creation response included an invalid Location header");
            throw new GlobalArgumentException(
                    HttpStatus.BAD_GATEWAY,
                    "The identity provider returned an invalid registration response"
            );
        }
        return keycloakUserId;
    }

}
