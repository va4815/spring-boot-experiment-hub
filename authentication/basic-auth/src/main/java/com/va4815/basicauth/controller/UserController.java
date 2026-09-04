package com.va4815.basicauth.controller;

import com.va4815.basicauth.config.authentication.AuthUserDetails;
import com.va4815.basicauth.dto.UserDTO;
import com.va4815.basicauth.entity.User;
import com.va4815.basicauth.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/me")
    public UserDTO getCurrentUser(Authentication authentication) {
        AuthUserDetails principal = (AuthUserDetails) authentication.getPrincipal();

        User user = userService.getUserByUsername(principal.getUsername());

        return UserDTO.fromUser(user);
    }

    @PostMapping
    public UserDTO createUser(@RequestBody UserDTO user) {
        User createdUser = userService.createUser(user);
        return UserDTO.fromUser(createdUser);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserDTO.fromUser(user);
    }

}
