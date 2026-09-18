package com.va4815.beareropaquestateful.service;

import com.va4815.beareropaquestateful.config.authentication.AuthUserCache;
import com.va4815.beareropaquestateful.dto.AuthRequestDTO;
import com.va4815.beareropaquestateful.dto.TokenResponseDTO;
import com.va4815.beareropaquestateful.dto.UserResponseDTO;
import com.va4815.beareropaquestateful.entity.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class AuthService {

    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private AuthUserCache authUserCache;

    public AuthService(PasswordEncoder passwordEncoder, AuthUserCache authUserCache,  UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.authUserCache = authUserCache;
        this.userService = userService;
    }

    public TokenResponseDTO login(AuthRequestDTO requestDTO) throws BadCredentialsException {
        if (!StringUtils.hasText(requestDTO.username()) || !StringUtils.hasText(requestDTO.password())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userService.getUserByUsername(requestDTO.username());

        if (!passwordEncoder.matches(requestDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = UUID.randomUUID().toString();

        authUserCache.login(token, user);

        return new TokenResponseDTO(token, user.getId());
    }

}
