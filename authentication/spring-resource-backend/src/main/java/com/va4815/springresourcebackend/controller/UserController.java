package com.va4815.springresourcebackend.controller;

import com.va4815.springresourcebackend.dto.request.UserLoginRequestDTO;
import com.va4815.springresourcebackend.dto.request.UserRegisterRequestDTO;
import com.va4815.springresourcebackend.dto.response.TokenResponseDTO;
import com.va4815.springresourcebackend.dto.response.UserResponseDTO;
import com.va4815.springresourcebackend.model.CurrentUser;
import com.va4815.springresourcebackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public CurrentUser getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return CurrentUser.from(jwt);
    }

    @PostMapping("/login")
    public TokenResponseDTO login(@RequestBody UserLoginRequestDTO requestDTO) {
        return userService.login(requestDTO);
    }

    @Operation(summary = "Register a Keycloak user account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User account created"),
            @ApiResponse(responseCode = "400", description = "Invalid registration details"),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "SHOP_ADMIN role is required"),
            @ApiResponse(responseCode = "409", description = "Account already exists"),
            @ApiResponse(responseCode = "502", description = "Identity provider rejected the operation"),
            @ApiResponse(responseCode = "503", description = "Identity provider is unavailable")
    })
    @PostMapping("/register")
    @PreAuthorize("hasRole('SHOP_ADMIN')")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO requestDTO) {
        UserResponseDTO userDTO = userService.register(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

}
