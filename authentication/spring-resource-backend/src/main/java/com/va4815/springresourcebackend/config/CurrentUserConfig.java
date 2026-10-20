package com.va4815.springresourcebackend.config;

import com.va4815.springresourcebackend.model.CurrentUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class CurrentUserConfig {

    @Bean
    @RequestScope
    public CurrentUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken auth)) {
            throw new AuthenticationCredentialsNotFoundException("No JWT authentication found");
        }

        return CurrentUser.from(auth.getToken());
    }

}
