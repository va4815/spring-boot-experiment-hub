package com.va4815.bearerjwtwithrefresh.controller;

import com.va4815.bearerjwtwithrefresh.dto.LoginRequestDTO;
import com.va4815.bearerjwtwithrefresh.dto.TokenResponseDTO;
import com.va4815.bearerjwtwithrefresh.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public TokenResponseDTO login(@RequestBody LoginRequestDTO requestDTO) {
        return authService.login(requestDTO);
    }

}
