package com.va4815.bearerjwtwithrefresh.controller;

import com.va4815.bearerjwtwithrefresh.dto.LoginRequestDTO;
import com.va4815.bearerjwtwithrefresh.dto.RefreshTokenRequestDTO;
import com.va4815.bearerjwtwithrefresh.dto.RefreshTokenResponseDTO;
import com.va4815.bearerjwtwithrefresh.dto.TokenResponseDTO;
import com.va4815.bearerjwtwithrefresh.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/refresh")
    public RefreshTokenResponseDTO refresh(@Valid @RequestBody RefreshTokenRequestDTO requestDTO) {
        return authService.refresh(requestDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequestDTO requestDTO) {
        authService.logout(requestDTO);
        return ResponseEntity.noContent().build();
    }

}
