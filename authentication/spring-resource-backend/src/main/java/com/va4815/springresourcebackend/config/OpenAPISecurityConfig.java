package com.va4815.springresourcebackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPISecurityConfig {

    @Value("${KC_SERVER_URL}")
    private String kcServerUrl;

    @Value("${KC_REALM}")
    private String kcRealm;

    private static final String OAUTH_SCHEME_NAME = "oauth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(OAUTH_SCHEME_NAME))
                .components(new Components()
                    .addSecuritySchemes(OAUTH_SCHEME_NAME, createOAuthSchema())
                );
    }

    private SecurityScheme createOAuthSchema() {
        OAuthFlows flows = createOAuthFlows();
        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(flows);
    }

    private OAuthFlows createOAuthFlows() {
        OAuthFlow flow = new OAuthFlow()
                .authorizationUrl(kcServerUrl + "/realms/" + kcRealm + "/protocol/openid-connect/auth")
                .tokenUrl(kcServerUrl + "/realms/" + kcRealm + "/protocol/openid-connect/token")
                .refreshUrl(kcServerUrl + "/realms/" + kcRealm + "/protocol/openid-connect/token")
                .scopes(new Scopes()
                        .addString("openid", "OpenID Connect")
                        .addString("profile", "User profile")
                )
                ;
        return new OAuthFlows().password(flow);
    }

}
