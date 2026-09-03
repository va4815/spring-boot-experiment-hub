package com.va4815.basicauth.config.authentication;

import com.va4815.basicauth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record SecurityUser(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    public SecurityUser {
        authorities = List.copyOf(authorities);
    }

    public static SecurityUser from(User user) {
        return new SecurityUser(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
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
}
