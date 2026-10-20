package com.va4815.springresourcebackend.model;

import org.springframework.security.oauth2.jwt.Jwt;

public record CurrentUser(String id, String username, String email) {

    public static CurrentUser from(Jwt jwt) {
        return new CurrentUser(
                jwt.getSubject(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email")
        );
    }

}