package com.va4815.springresourcebackend.service;

import com.va4815.springresourcebackend.dto.request.UserLoginRequestDTO;
import com.va4815.springresourcebackend.dto.response.TokenResponseDTO;
import com.va4815.springresourcebackend.repository.UserRepository;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${KC_SERVER_URL}")
    private String kcServerUrl;

    @Value("${KC_REALM}")
    private String kcRealm;

    @Value("${KC_CLIENT_ID}")
    private String kcClientId;

    @Value("${KC_CLIENT_SECRET}")
    private String kcClientSecret;


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

}
