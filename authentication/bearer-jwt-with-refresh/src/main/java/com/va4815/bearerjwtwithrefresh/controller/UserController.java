package com.va4815.bearerjwtwithrefresh.controller;

import com.va4815.bearerjwtwithrefresh.config.authentication.AuthUserDetails;
import com.va4815.bearerjwtwithrefresh.dto.CreateUserRequestDTO;
import com.va4815.bearerjwtwithrefresh.dto.UserDTO;
import com.va4815.bearerjwtwithrefresh.entity.User;
import com.va4815.bearerjwtwithrefresh.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public UserDTO register(@RequestBody CreateUserRequestDTO requestDTO) {
        User user = userService.createUser(requestDTO);
        return UserDTO.fromUser(user);
    }

}
