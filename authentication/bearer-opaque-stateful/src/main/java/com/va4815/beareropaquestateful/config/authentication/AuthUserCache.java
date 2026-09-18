package com.va4815.beareropaquestateful.config.authentication;

import com.va4815.beareropaquestateful.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthUserCache {

    private static final Map<String, User> sessions = new ConcurrentHashMap<>();

    public void login(String token, User user) {
        sessions.put(token, user);
    }

    public void logout(String token) {
        sessions.remove(token);
    }

    public Optional<User> getUserByToken(String token) {
        return Optional.ofNullable(sessions.get(token));
    }

}
