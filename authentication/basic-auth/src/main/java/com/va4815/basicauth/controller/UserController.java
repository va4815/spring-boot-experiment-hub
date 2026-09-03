package com.va4815.basicauth.controller;

import com.va4815.basicauth.dto.UserDTO;
import com.va4815.basicauth.entity.User;
import com.va4815.basicauth.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
