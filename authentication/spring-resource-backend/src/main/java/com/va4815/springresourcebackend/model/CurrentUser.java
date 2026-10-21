package com.va4815.springresourcebackend.model;

import org.springframework.security.oauth2.jwt.Jwt;

public class CurrentUser {

    private final String id;
    private final String username;
    private final String email;

    public CurrentUser(
            String id,
            String username,
            String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public static CurrentUser from(Jwt jwt) {
        return new CurrentUser(
                jwt.getSubject(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email")
        );
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
