package com.va4815.springresourcebackend.controller;

import com.va4815.springresourcebackend.model.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/me")
    public CurrentUser getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return CurrentUser.from(jwt);
    }

}
