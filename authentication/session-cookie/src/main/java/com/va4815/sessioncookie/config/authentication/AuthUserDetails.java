package com.va4815.sessioncookie.config.authentication;

import com.va4815.sessioncookie.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record AuthUserDetails(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {
    private static final String ROLE_PREFIX = "ROLE_";

    public AuthUserDetails {
        authorities = List.copyOf(authorities);
    }

    public static AuthUserDetails from(User user) {
        return new AuthUserDetails(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(ROLE_PREFIX + user.getRole().getCode()))
        );
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String toString() {
        return "AuthUserDetails[username=" + username + ", authorities=" + authorities + "]";
    }
}
