package com.va4815.springresourcebackend.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminClientConfig {

    @Bean(destroyMethod = "close")
    Keycloak keycloakAdminClient(
            @Value("${KC_SERVER_URL}") String serverUrl,
            @Value("${KC_REALM}") String realm,
            @Value("${KC_CLIENT_ID}") String clientId,
            @Value("${KC_CLIENT_SECRET}") String clientSecret
    ) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}
