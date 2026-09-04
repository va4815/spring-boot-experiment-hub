package com.va4815.basicauth.controller;

import com.va4815.basicauth.config.authentication.AuthUserDetails;
import com.va4815.basicauth.dto.AuthDTO;
import com.va4815.basicauth.dto.UserDTO;
import com.va4815.basicauth.entity.User;
import com.va4815.basicauth.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public UserDTO login(@RequestBody AuthDTO authDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(), authDTO.getPassword())
        );

        AuthUserDetails principal = (AuthUserDetails) authentication.getPrincipal();

        User user = userService.getUserByUsername(principal.getUsername());

        return UserDTO.fromUser(user);
    }

}
