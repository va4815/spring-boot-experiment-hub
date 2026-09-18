package com.va4815.beareropaquestateful.controller;

import com.va4815.beareropaquestateful.dto.AuthRequestDTO;
import com.va4815.beareropaquestateful.dto.TokenResponseDTO;
import com.va4815.beareropaquestateful.dto.UserResponseDTO;
import com.va4815.beareropaquestateful.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponseDTO login(
            @RequestBody AuthRequestDTO requestDTO) {

        return authService.login(requestDTO);
    }

}
