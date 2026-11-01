package com.va4815.springresourcebackend.controller;

import com.va4815.springresourcebackend.dto.request.UserLoginRequestDTO;
import com.va4815.springresourcebackend.dto.response.TokenResponseDTO;
import com.va4815.springresourcebackend.model.CurrentUser;
import com.va4815.springresourcebackend.service.UserService;
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

}
