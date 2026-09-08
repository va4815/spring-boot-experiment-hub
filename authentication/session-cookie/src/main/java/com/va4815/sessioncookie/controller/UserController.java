package com.va4815.sessioncookie.controller;

import com.va4815.sessioncookie.config.authentication.AuthUserDetails;
import com.va4815.sessioncookie.dto.UserDTO;
import com.va4815.sessioncookie.entity.User;
import com.va4815.sessioncookie.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserDTO me(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        User user = userService.getUserByUsername(authUserDetails.getUsername());
        return UserDTO.fromUser(user);
    }

}
